package flowershop.controllers;

import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.services.ReportService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

import javafx.scene.control.*;
import javafx.scene.chart.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;

public class AdminReportsController {

    @FXML private Label totalProducts;
    @FXML private Label totalOrders;
    @FXML private Label totalCustomers;
    @FXML private Label totalProfit;
    @FXML private Label avgOrderValue;
    @FXML private Label totalRevenue;

    @FXML private TableView<Map<String, Object>> recentOrdersTable;
    @FXML private TableView<Map<String, Object>> topCustomersTable;
    @FXML private TableView<Map<String, Object>> lowStockTable;

    @FXML private BarChart<String,Number> revenueChart;
    @FXML private LineChart<String, Number> trendChart;
    @FXML private PieChart topProductsChart;

    @FXML private ComboBox<String> filterRange;
    @FXML private DatePicker fromDate, toDate;
    // ===== RECENT ORDERS =====
    @FXML private TableColumn<Map<String,Object>, String> colOrderId;
    @FXML private TableColumn<Map<String,Object>, String> colCustomer;
    @FXML private TableColumn<Map<String,Object>, String> colDate;
    @FXML private TableColumn<Map<String,Object>, String> colTotal;
    @FXML private TableColumn<Map<String,Object>, String> colStatus;

    // ===== TOP CUSTOMERS =====
    @FXML private TableColumn<Map<String,Object>, String> colCusName;
    @FXML private TableColumn<Map<String,Object>, String> colCusOrders;
    @FXML private TableColumn<Map<String,Object>, String> colCusSpent;

    // ===== LOW STOCK =====
    @FXML private TableColumn<Map<String,Object>, String> colProductName;
    @FXML private TableColumn<Map<String,Object>, String> colStockQty;

    private ReportService reportService = new ReportService();

    // ================= INIT =================
    @FXML
    public void initialize(){
        setupFilter();
        recentOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        topCustomersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lowStockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colOrderId.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("id"))));

        colCustomer.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("customer"))));

        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("date"))));

        colTotal.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("total"))));

        colStatus.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("status"))));


        colCusName.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("name"))));

        colCusOrders.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("orders"))));

        colCusSpent.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("spent"))));


        colProductName.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("product"))));

        colStockQty.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().get("qty"))));

        loadDashboardData();
    }

    private void setupFilter(){
        filterRange.setItems(FXCollections.observableArrayList(
                "Today","This Week","This Month","This Year"
        ));
    }

    // ================= MAIN =================
    private void loadDashboardData(){
        loadSummary();
        loadRevenueChart();
        loadTrendChart();
        loadRecentOrders();
        loadTopCustomers();
        loadLowStock();
        loadTopProductsChart();
    }

    // ================= SUMMARY =================
    private void loadSummary(){

        double revenue = reportService.getTotalRevenue();

        totalProducts.setText(String.valueOf(reportService.countProducts()));
        totalOrders.setText(String.valueOf(reportService.countOrders()));
        totalCustomers.setText(String.valueOf(reportService.countCustomers()));

        totalRevenue.setText(String.format("$%.2f", revenue));
        totalProfit.setText(String.format("$%.2f", revenue * 0.3));
        avgOrderValue.setText(String.format("$%.2f", reportService.getAverageOrderValue()));
    }

    // ================= BAR CHART =================
    private void loadRevenueChart(){

        revenueChart.getData().clear();

        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        for(int i = 1; i <= 12; i++){
            series.getData().add(
                    new XYChart.Data<>(getMonthName(i), reportService.getRevenue(i))
            );
        }

        revenueChart.getData().add(series);
    }

    // ================= LINE CHART =================
    private void loadTrendChart(){

        trendChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders Trend");

        Map<String, Integer> data = reportService.getOrdersTrend();

        for(String key : data.keySet()){
            series.getData().add(new XYChart.Data<>(key, data.get(key)));
        }

        trendChart.getData().add(series);
    }

    // ================= PIE CHART =================

    private void loadTopProductsChart(){

        topProductsChart.getData().clear();

        List<Map<String, Object>> list = reportService.getLowStock();
        // hoặc viết riêng getTopProducts nếu muốn xịn hơn

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for(Map<String,Object> item : list){
            String name = String.valueOf(item.get("product"));
            int qty = Integer.parseInt(String.valueOf(item.get("qty")));

            pieData.add(new PieChart.Data(name, qty));
        }
        topProductsChart.setTitle("Top Products");

        for(PieChart.Data data : topProductsChart.getData()){
            data.nameProperty().bind(
                    javafx.beans.binding.Bindings.concat(
                            data.getName(), " (", data.pieValueProperty(), ")"
                    )
            );
        }

        topProductsChart.setData(pieData);
    }

    // ================= TABLE =================

    private void loadRecentOrders(){
        List<Map<String,Object>> list = reportService.getRecentOrders();
        recentOrdersTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadTopCustomers(){
        List<Map<String,Object>> list = reportService.getTopCustomers();
        topCustomersTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadLowStock(){
        List<Map<String,Object>> list = reportService.getLowStock();
        lowStockTable.setItems(FXCollections.observableArrayList(list));
    }

    // ================= UTIL =================
    private String getMonthName(int month){
        return switch (month) {
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

    // ================= ACTION =================

    @FXML
    private void handleRefresh(){
        loadDashboardData();
    }

    @FXML
    private void handleFilter(){

        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();

        // 👉 Nếu chọn nhanh bằng combo
        String range = filterRange.getValue();

        if(range != null){
            LocalDate now = LocalDate.now();

            switch (range){
                case "Today" -> {
                    from = now;
                    to = now;
                }
                case "This Week" -> {
                    from = now.minusDays(7);
                    to = now;
                }
                case "This Month" -> {
                    from = now.withDayOfMonth(1);
                    to = now;
                }
                case "This Year" -> {
                    from = now.withDayOfYear(1);
                    to = now;
                }
            }
        }

        // ❗ validate
        if(from == null || to == null){
            System.out.println("Chưa chọn ngày");
            return;
        }

        // 👉 set vào service
        reportService.setDateRange(from, to);

        // 👉 reload
        loadDashboardData();
    }

    @FXML
    private void handleExport(){
        System.out.println("Exporting report...");
    }

    // ================= NAV =================

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

    @FXML public void goEmployees(){
        SceneManager.switchScene("/fxml/AdminEmployees.fxml","Employees");
    }

    @FXML void goReports(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminReports.fxml","Reports");
    }

    @FXML
    void handleLogout(ActionEvent e){
        SessionManager.clear();
        SceneManager.switchScene("/fxml/login.fxml","Login");
    }
}