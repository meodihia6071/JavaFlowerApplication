package flowershop.controllers;

import flowershop.dao.StockDAO;
import flowershop.models.Stock;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AdminStockController {

    @FXML
    private TableView<Stock> stockTable;

    @FXML
    private TableColumn<Stock,Integer> colId;

    @FXML
    private TableColumn<Stock,String> colProduct;

    @FXML
    private TableColumn<Stock,Integer> colQuantity;

    @FXML
    private TableColumn<Stock,String> colUpdate;

    @FXML
    private TextField searchField;

    private StockDAO stockDAO = new StockDAO();

    private ObservableList<Stock> stockList;

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));

        loadStock();
    }

    private void loadStock(){

        List<Stock> list = stockDAO.getAllStock();
        stockList = FXCollections.observableArrayList(list);

        stockTable.setItems(stockList);
    }

    // ================= NAVIGATION =================

    @FXML
    public void goDashboard(ActionEvent event){
        SceneManager.switchScene("/fxml/AdminDashboard.fxml","Dashboard");
    }

    @FXML
    public void goProducts(ActionEvent event){
        SceneManager.switchScene("/fxml/AdminProducts.fxml","Products");
    }

    @FXML
    public void goCategories(ActionEvent event){
        SceneManager.switchScene("/fxml/AdminCategories.fxml","Categories");
    }

    @FXML
    public void goOrders(ActionEvent event){
        SceneManager.switchScene("/fxml/AdminOrders.fxml","Orders");
    }

    @FXML
    public void goCustomers(ActionEvent event){
        SceneManager.switchScene("/fxml/AdminCustomers.fxml","Customers");
    }

    @FXML
    public void goSuppliers(ActionEvent event){
        SceneManager.switchScene("/fxml/AdminSuppliers.fxml","Suppliers");
    }

    @FXML
    public void goStock(ActionEvent event){
        SceneManager.switchScene("/fxml/AdminStock.fxml","Stock");
    }

    @FXML
    public void goReports(ActionEvent event){
        SceneManager.switchScene("/fxml/AdminReports.fxml","Reports");
    }

    @FXML
    public void handleLogout(ActionEvent event){
        SessionManager.clear();
        SceneManager.switchScene("/fxml/login.fxml","Login");
    }

    // ================= CRUD =================

    @FXML
    public void handleAddStock(ActionEvent event){

        Stock stock = new Stock();
        stock.setProductName("New Flower");
        stock.setQuantity(10);
        stock.setLastUpdate("2026");

        stockDAO.save(stock);

        loadStock();
    }

    @FXML
    public void handleEditStock(ActionEvent event){

        Stock selected = stockTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Select stock first");
            alert.show();
            return;
        }

        selected.setQuantity(selected.getQuantity()+1);

        stockDAO.update(selected);

        loadStock();
    }

    @FXML
    public void handleDeleteStock(ActionEvent event){

        Stock selected = stockTable.getSelectionModel().getSelectedItem();

        if(selected == null) return;

        stockDAO.delete(selected);

        loadStock();
    }

    // ================= SEARCH + SORT =================

    @FXML
    public void handleSort(ActionEvent event){

        FXCollections.sort(stockList,
                (a,b)->a.getProductName().compareToIgnoreCase(b.getProductName()));
    }

    @FXML
    public void handleSearch(ActionEvent event){

        String keyword = searchField.getText();

        if(keyword == null || keyword.isEmpty()){
            stockTable.setItems(stockList);
            return;
        }

        keyword = keyword.toLowerCase();

        ObservableList<Stock> filtered = FXCollections.observableArrayList();

        for(Stock s : stockList){

            if(s.getProductName().toLowerCase().contains(keyword)){
                filtered.add(s);
            }
        }

        stockTable.setItems(filtered);
    }
}