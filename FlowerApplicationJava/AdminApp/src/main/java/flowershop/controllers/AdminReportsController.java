package flowershop.controllers;

import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.services.ReportService;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.chart.*;
import javafx.scene.control.TableView;

public class AdminReportsController {

    @FXML private Label totalProducts;
    @FXML private Label totalOrders;
    @FXML private Label totalCustomers;
    @FXML private Label totalProfit;
    @FXML private Label avgOrderValue;
    @FXML private TableView<?> recentOrdersTable;
    @FXML private TableView<?> topCustomersTable;
    @FXML private TableView<?> lowStockTable;
    @FXML private Label totalRevenue;

    @FXML private BarChart<String,Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    @FXML private LineChart<String, Number> trendChart;

    @FXML private ComboBox<String> filterRange;
    @FXML private DatePicker fromDate, toDate;

    private ReportService reportService = new ReportService();

    // ================= INIT =================
    @FXML
    public void initialize(){
        loadDashboardData();
        recentOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        topCustomersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        lowStockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ================= MAIN LOAD =================
    private void loadDashboardData(){
        loadSummary();
        loadRevenueChart();
        loadTrendChart();
    }

    // ================= SUMMARY =================
    private void loadSummary(){

        totalProducts.setText(String.valueOf(reportService.countProducts()));
        totalOrders.setText(String.valueOf(reportService.countOrders()));
        totalCustomers.setText(String.valueOf(reportService.countCustomers()));

        // 🔥 Nếu chưa có trong service thì tạm hardcode
        totalProfit.setText("$" + reportService.getTotalRevenue() * 0.3);
        avgOrderValue.setText("$" + reportService.getAverageOrderValue());
        double revenue = reportService.getTotalRevenue();
        totalRevenue.setText(String.format("$%.2f", revenue));
    }

    // ================= BAR CHART =================
    private void loadRevenueChart(){

        revenueChart.getData().clear();

        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        for(int i = 1; i <= 6; i++){
            series.getData().add(
                    new XYChart.Data<>(getMonthName(i), reportService.getRevenue(i))
            );
        }

        revenueChart.getData().add(series);
    }

    // ================= LINE CHART =================
    private void loadTrendChart(){

        if(trendChart == null) return;

        trendChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders Trend");

        series.getData().add(new XYChart.Data<>("Week 1", reportService.countOrders()));
        series.getData().add(new XYChart.Data<>("Week 2", reportService.countOrders() + 2));
        series.getData().add(new XYChart.Data<>("Week 3", reportService.countOrders() + 1));

        trendChart.getData().add(series);
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
            default -> "";
        };
    }

    // ================= ACTIONS =================
    @FXML
    private void handleRefresh() {
        loadDashboardData(); // ✅ FIX LỖI Ở ĐÂY
    }

    @FXML
    private void handleExport() {
        System.out.println("Exporting report...");
    }

    // ================= NAVIGATION =================
    @FXML
    void goDashboard(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminDashboard.fxml","Dashboard");
    }

    @FXML
    void goProducts(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminProducts.fxml","Products");
    }

    @FXML
    void goCategories(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminCategories.fxml","Categories");
    }

    @FXML
    void goOrders(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminOrders.fxml","Orders");
    }

    @FXML
    void goCustomers(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminCustomers.fxml","Customers");
    }

    @FXML
    void goSuppliers(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminSuppliers.fxml","Suppliers");
    }

    @FXML
    void goStock(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminStock.fxml","Stock");
    }

    @FXML
    void goReports(ActionEvent e){
        SceneManager.switchScene("/fxml/AdminReports.fxml","Reports");
    }

    @FXML
    void handleLogout(ActionEvent e){
        SessionManager.clear();
        SceneManager.switchScene("/fxml/login.fxml","Login");
    }
}