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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.*;
import javafx.util.Duration;

public class AdminReportsController {

    // ================= FORMAT =================
    private final NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    private String formatVND(double amount) {
        return vnFormat.format(amount) + "₫";
    }

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

    @FXML
    private ImageView assistantImage;

    @FXML
    private Label assistantText;

    // ================= FILTER =================
    @FXML private ComboBox<String> filterRange;
    @FXML private DatePicker fromDate, toDate;

    private final ReportService reportService = new ReportService();

    // ================= INIT =================
    @FXML
    public void initialize(){
        setupTableColumns();
        setupFilter();
        loadDashboardData();
    }

    // ================= SETUP =================
    private void setupTableColumns(){

        // ===== Top Customers =====
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

        // 🔥 FORMAT TIỀN NGAY TRONG TABLE
        colCusSpent.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(formatVND(value.doubleValue()));
                }
            }
        });

        // ===== Low Stock =====
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

    private void setupFilter(){
        filterRange.setItems(FXCollections.observableArrayList(
                "Today", "This Week", "This Month", "This Year"
        ));
    }

    // ================= MAIN LOAD =================
    private void loadDashboardData(){
        loadSummary();
        loadRevenueChart();
        loadTrendChart();
        loadTopProductsChart();
        loadTopCustomers();
        loadLowStock();
        updateAssistant();
    }

    // ================= SUMMARY =================
    private void loadSummary(){

        int products = reportService.countProducts();
        int orders = reportService.countOrders();
        int customers = reportService.countCustomers();

        double revenue = reportService.getTotalRevenue();
        double avgOrder = reportService.getAverageOrderValue();
        double profit = revenue * 0.3;

        totalProducts.setText(String.valueOf(products));
        totalOrders.setText(String.valueOf(orders));
        totalCustomers.setText(String.valueOf(customers));

        // 🔥 FORMAT TIỀN VIỆT
        totalRevenue.setText(formatVND(revenue));
        avgOrderValue.setText(formatVND((long) avgOrder));
        totalProfit.setText(formatVND(profit));
    }

    // ================= BAR CHART =================
    private void loadRevenueChart(){

        revenueChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        Map<Integer, Double> data = reportService.getRevenueByMonth();

        data.forEach((month, value) -> {
            series.getData().add(
                    new XYChart.Data<>(getMonthName(month), value)
            );
        });

        revenueChart.getData().add(series);
    }

    // ================= LINE CHART =================
    private void loadTrendChart(){

        trendChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders");

        Map<String, Integer> data = reportService.getOrdersByWeek();

        data.forEach((week, value) -> {
            series.getData().add(
                    new XYChart.Data<>(week, value)
            );
        });

        trendChart.getData().add(series);
    }

    // ================= PIE =================
    private void loadTopProductsChart(){

        topProductsChart.getData().clear();

        Map<String, Integer> data = reportService.getTopProducts();

        data.forEach((name, value) -> {
            topProductsChart.getData().add(
                    new PieChart.Data(name, value)
            );
        });
    }

    // ================= TABLE =================
    private void loadTopCustomers(){
        topCustomersTable.setItems(
                FXCollections.observableArrayList(
                        reportService.getTopCustomers()
                )
        );
    }

    private void loadLowStock(){
        lowStockTable.setItems(
                FXCollections.observableArrayList(
                        reportService.getLowStock()
                )
        );
    }

    // ================= UTIL =================
    private String getMonthName(int month){
        return switch (month){
            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";
            default -> "";
        };
    }
    private void updateAssistant() {

        int lowStockCount = lowStockTable.getItems().size();

        if (lowStockCount == 0) {
            // 🌸 Trạng thái tốt
            assistantImage.setImage(new Image(
                    getClass().getResource("/images/no.gif").toExternalForm()
            ));

            typeText("Hôm nay yên bình 🌿 Kho hàng ổn định!");

        } else if (lowStockCount <= 3) {
            // ⚠️ Cảnh báo nhẹ
            assistantImage.setImage(new Image(
                    getClass().getResource("/images/happy.gif").toExternalForm()
            ));

            typeText("Có vài hoa sắp hết rồi đó!");

        } else {
            // 🚨 Nguy hiểm
            assistantImage.setImage(new Image(
                    getClass().getResource("/images/sad.gif").toExternalForm()
            ));

            typeText("Toang rồi! Kho đang cạn! Nhập hàng gấp!");
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
        loadDashboardData();
    }

    @FXML
    private void handleExport() {
        System.out.println("Exporting report...");
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

    @FXML void goEmployees(){
        SceneManager.switchScene("/fxml/AdminEmployees.fxml","Employees");
    }

    @FXML void goReports(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminReports.fxml","Reports");
    }

    @FXML void handleLogout(ActionEvent e){
        SessionManager.clear();
        SceneManager.switchScene("/fxml/login.fxml","Login");
    }
}