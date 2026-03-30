package flowershop.controllers;

import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.services.ReportService;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;

import java.util.Map;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.*;
import javafx.util.Duration;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AdminReportsController {

    // ================= FORMAT =================
    private final NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    private String formatVND(double amount) {
        return vnFormat.format(amount) + "₫";
    }

    // ================= DATE FILTER =================
    private LocalDate startDate;
    private LocalDate endDate;

    // ================= LABEL =================
    @FXML private Label totalProducts;
    @FXML private Label totalOrders;
    @FXML private Label totalCustomers;
    @FXML private Label totalProfit;
    @FXML private Label avgOrderValue;
    @FXML private Label totalRevenue;

    // ================= TABLE =================
    @FXML private TableView<Map<String, Object>> topCustomersTable;
    @FXML private TableView<Map<String, Object>> lowStockTable;

    @FXML private TableColumn<Map<String,Object>, String> colCusName;
    @FXML private TableColumn<Map<String,Object>, Number> colCusOrders;
    @FXML private TableColumn<Map<String,Object>, Number> colCusSpent;

    @FXML private TableColumn<Map<String,Object>, String> colProductName;
    @FXML private TableColumn<Map<String,Object>, Number> colStockQty;

    // ================= CHART =================
    @FXML private BarChart<String,Number> revenueChart;
    @FXML private LineChart<String, Number> trendChart;
    @FXML private PieChart topProductsChart;

    @FXML private ImageView assistantImage;
    @FXML private Label assistantText;

    // ================= FILTER =================
    @FXML private DatePicker fromDate, toDate;

    private final ReportService reportService = new ReportService();

    // ================= INIT =================
    @FXML
    public void initialize(){
        setupTableColumns();

        // Load toàn bộ dashboard (không filter)
        loadDashboardStaticData();

        // Load riêng phần có filter (3 ô)
        loadFilteredSummaryOnly();

        // ======= LISTENER DATE PICKERS =======
        fromDate.valueProperty().addListener((obs, oldVal, newVal) -> updateDateFilter());
        toDate.valueProperty().addListener((obs, oldVal, newVal) -> updateDateFilter());
    }

    // ================= SETUP =================
    private void setupTableColumns(){

        colCusName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        (String) data.getValue().get("name"))
        );

        colCusOrders.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        ((Number) data.getValue().get("orders")).intValue())
        );

        colCusSpent.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        ((Number) data.getValue().get("spent")).doubleValue())
        );

        colCusSpent.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatVND(value.doubleValue()));
            }
        });

        colProductName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        (String) data.getValue().get("product"))
        );

        colStockQty.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        ((Number) data.getValue().get("quantity")).intValue())
        );

        topCustomersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lowStockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ================= LOAD STATIC =================
    private void loadDashboardStaticData(){
        loadSummaryStatic();
        loadRevenueChart();
        loadTrendChart();
        loadTopProductsChart();
        loadTopCustomers();
        loadLowStock();
        updateAssistant();
    }

    private void updateDateFilter(){
        startDate = fromDate.getValue();
        endDate = toDate.getValue();

        if(startDate != null && endDate != null){
            if(startDate.isAfter(endDate)){
                new Alert(Alert.AlertType.WARNING, "Ngày bắt đầu phải trước ngày kết thúc!").show();
                return;
            }
            loadSummaryStatic();
            loadFilteredSummaryOnly();
        }
    }

    // ================= SUMMARY =================
    private void loadSummaryStatic(){
        int products = reportService.countProducts();
        int orders = reportService.countOrders(startDate, endDate);
        int customers = reportService.countCustomers(startDate, endDate);

        totalProducts.setText(String.valueOf(products));
        totalOrders.setText(String.valueOf(orders));
        totalCustomers.setText(String.valueOf(customers));
    }

    private void loadFilteredSummaryOnly(){
        double revenue = reportService.getTotalRevenue(startDate, endDate);
        double cost = reportService.getTotalCost(startDate, endDate);
        double profit = revenue - cost;

        long avgOrderInt = Math.round(
                reportService.getAverageOrderValue(startDate, endDate)
        );

        totalRevenue.setText(formatVND(revenue));
        totalProfit.setText(formatVND(profit));
        avgOrderValue.setText(formatVND(avgOrderInt));
    }

    // ================= CHART =================
    private void loadRevenueChart() {
        revenueChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        Map<Integer, Double> data = reportService.getRevenueByMonth();

        data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        series.getData().add(
                                new XYChart.Data<>(getMonthName(entry.getKey()), entry.getValue())
                        )
                );

        revenueChart.getData().add(series);
    }

    private void loadTrendChart(){
        trendChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders");

        Map<String, Integer> data = reportService.getOrdersByLast10Weeks();

        data.forEach((week, count) ->
                series.getData().add(new XYChart.Data<>(week, count))
        );

        trendChart.getData().add(series);
    }

    private void loadTopProductsChart(){
        topProductsChart.getData().clear();

        Map<String, Integer> data = reportService.getTopProducts(null, null);

        data.forEach((name, value) ->
                topProductsChart.getData().add(new PieChart.Data(name, value))
        );
    }

    // ================= TABLE =================
    private void loadTopCustomers(){
        topCustomersTable.setItems(
                FXCollections.observableArrayList(reportService.getTopCustomers())
        );
    }

    private void loadLowStock(){
        lowStockTable.setItems(
                FXCollections.observableArrayList(reportService.getLowStock())
        );
    }

    // ================= UTIL =================
    private String getMonthName(int month){
        return switch (month){
            case 1 -> "Jan"; case 2 -> "Feb"; case 3 -> "Mar";
            case 4 -> "Apr"; case 5 -> "May"; case 6 -> "Jun";
            case 7 -> "Jul"; case 8 -> "Aug"; case 9 -> "Sep";
            case 10 -> "Oct"; case 11 -> "Nov"; case 12 -> "Dec";
            default -> "";
        };
    }

    private void updateAssistant() {

        int lowStockCount = lowStockTable.getItems().size();

        if (lowStockCount == 0) {
            assistantImage.setImage(new Image(
                    getClass().getResource("/images/no.gif").toExternalForm()
            ));
            typeText("Kho ổn định 🌿");
        } else if (lowStockCount <= 3) {
            assistantImage.setImage(new Image(
                    getClass().getResource("/images/happy.gif").toExternalForm()
            ));
            typeText("Sắp hết hàng rồi!");
        } else {
            assistantImage.setImage(new Image(
                    getClass().getResource("/images/sad.gif").toExternalForm()
            ));
            typeText("Kho cạn! Nhập gấp!");
        }
    }

    private void typeText(String text) {
        assistantText.setText("");

        Timeline timeline = new Timeline();

        for (int i = 0; i < text.length(); i++) {
            final int index = i;

            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(40 * i),
                    e -> assistantText.setText(text.substring(0, index + 1))
            );

            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
    }

    // ================= ACTION =================
    @FXML
    private void handleRefresh() {
        startDate = null;
        endDate = null;

        fromDate.setValue(null);
        toDate.setValue(null);

        // load All Time
        loadFilteredSummaryOnly();
    }

    @FXML
    private void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        try (FileWriter writer = new FileWriter(file)) {

            writer.write("Flower Shop Report\n");
            writer.write("From: " + startDate + " To: " + endDate + "\n\n");

            writer.write("Revenue," + totalRevenue.getText() + "\n");
            writer.write("Profit," + totalProfit.getText() + "\n");
            writer.write("Avg Order," + avgOrderValue.getText() + "\n");

            writer.flush();

            new Alert(Alert.AlertType.INFORMATION, "Export thành công!").show();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Lỗi export!").show();
        }
    }

    // ================= NAVIGATION =================
    @FXML void goDashboard(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminDashboard.fxml","Dashboard");
    }

    @FXML void goProducts(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminProducts.fxml","Products");
    }

    @FXML void goCategories(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminCategories.fxml","Categories");
    }

    @FXML void goOrders(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminOrders.fxml","Orders");
    }

    @FXML void goCustomers(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminCustomers.fxml","Customers");
    }

    @FXML void goSuppliers(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminSuppliers.fxml","Suppliers");
    }

    @FXML void goStock(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminStock.fxml","Stock");
    }

    @FXML void goReports(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminReports.fxml","Reports");
    }

    @FXML void handleLogout(ActionEvent e){
        SessionManager.clear();
        SceneManager.switchScene("/fxml/login.fxml","Login");
    }
}