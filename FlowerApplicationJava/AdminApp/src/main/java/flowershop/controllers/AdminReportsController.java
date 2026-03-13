package flowershop.controllers;

import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.services.ReportService;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;

import javafx.scene.control.Label;
import javafx.scene.chart.*;

public class AdminReportsController {

    @FXML
    private Label totalProducts;

    @FXML
    private Label totalOrders;

    @FXML
    private Label totalCustomers;

    @FXML
    private BarChart<String,Number> revenueChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private ReportService reportService = new ReportService();

    @FXML
    public void initialize(){

        loadSummary();
        loadChart();
    }

    private void loadSummary(){

        totalProducts.setText(String.valueOf(reportService.countProducts()));
        totalOrders.setText(String.valueOf(reportService.countOrders()));
        totalCustomers.setText(String.valueOf(reportService.countCustomers()));
    }

    private void loadChart(){

        XYChart.Series<String,Number> series = new XYChart.Series<>();
        series.setName("Monthly Revenue");

        series.getData().add(new XYChart.Data<>("Jan", reportService.getRevenue(1)));
        series.getData().add(new XYChart.Data<>("Feb", reportService.getRevenue(2)));
        series.getData().add(new XYChart.Data<>("Mar", reportService.getRevenue(3)));
        series.getData().add(new XYChart.Data<>("Apr", reportService.getRevenue(4)));
        series.getData().add(new XYChart.Data<>("May", reportService.getRevenue(5)));
        series.getData().add(new XYChart.Data<>("Jun", reportService.getRevenue(6)));

        revenueChart.getData().add(series);
    }

    // ===== Navigation =====

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
