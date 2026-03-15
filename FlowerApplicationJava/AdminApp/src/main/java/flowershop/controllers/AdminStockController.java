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

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.util.List;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;

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
    private TableColumn<Stock,String> colSupplier;

    @FXML
    private TableColumn<Stock,Double> colPrice;

    @FXML
    private TableColumn<Stock,String> colImportDate;

    @FXML
    private TableColumn<Stock,String> colUpdate;

    @FXML
    private TextField searchField;

    @FXML
    private VBox sidebar;

    private StockDAO stockDAO = new StockDAO();

    private ObservableList<Stock> stockList;

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colImportDate.setCellValueFactory(new PropertyValueFactory<>("importDate"));
        colUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));


        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        stockTable.setRowFactory(tv -> {
            TableRow<Stock> row = new TableRow<>();
            row.setPrefHeight(35);
            return row;
        });

        loadStock();

        // ===== SET ACTIVE MENU =====
        setActiveMenu("Stock");
    }

    private void loadStock(){

        List<Stock> list = stockDAO.getAllStock();
        stockList = FXCollections.observableArrayList(list);

        stockTable.setItems(stockList);
    }

    // ===== ACTIVE MENU =====

    private void setActiveMenu(String name){

        if(sidebar == null) return;

        for(Node node : sidebar.getChildren()){

            if(node instanceof Button){

                Button btn = (Button) node;

                btn.getStyleClass().remove("menu-active");

                if(btn.getText().equalsIgnoreCase(name)){
                    btn.getStyleClass().add("menu-active");
                }
            }
        }
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

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Stock");

        Label nameLabel = new Label("Product:");
        TextField nameField = new TextField();

        Label qtyLabel = new Label("Quantity:");
        TextField qtyField = new TextField();

        Label supplierLabel = new Label("Supplier:");
        TextField supplierField = new TextField();

        Label priceLabel = new Label("Price:");
        TextField priceField = new TextField();

        Label importLabel = new Label("Import Date:");
        TextField importField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(nameLabel,0,0);
        grid.add(nameField,1,0);

        grid.add(qtyLabel,0,1);
        grid.add(qtyField,1,1);

        grid.add(supplierLabel,0,2);
        grid.add(supplierField,1,2);

        grid.add(priceLabel,0,3);
        grid.add(priceField,1,3);

        grid.add(importLabel,0,4);
        grid.add(importField,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            Stock stock = new Stock();

            stock.setProductName(nameField.getText());
            stock.setQuantity(Integer.parseInt(qtyField.getText()));
            stock.setSupplier(supplierField.getText());
            stock.setPrice(Double.parseDouble(priceField.getText()));
            stock.setImportDate(importField.getText());
            stock.setLastUpdate(java.time.LocalDate.now().toString());

            stockDAO.save(stock);

            loadStock();
        }
    }

    @FXML
    public void handleEditStock(ActionEvent event){

        Stock selected = stockTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select stock to edit");
            alert.show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Stock");

        Label nameLabel = new Label("Product:");
        TextField nameField = new TextField(selected.getProductName());

        Label qtyLabel = new Label("Quantity:");
        TextField qtyField = new TextField(String.valueOf(selected.getQuantity()));

        Label supplierLabel = new Label("Supplier:");
        TextField supplierField = new TextField(selected.getSupplier());

        Label priceLabel = new Label("Price:");
        TextField priceField = new TextField(String.valueOf(selected.getPrice()));

        Label importLabel = new Label("Import Date:");
        TextField importField = new TextField(selected.getImportDate());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(nameLabel,0,0);
        grid.add(nameField,1,0);

        grid.add(qtyLabel,0,1);
        grid.add(qtyField,1,1);

        grid.add(supplierLabel,0,2);
        grid.add(supplierField,1,2);

        grid.add(priceLabel,0,3);
        grid.add(priceField,1,3);

        grid.add(importLabel,0,4);
        grid.add(importField,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            selected.setProductName(nameField.getText());
            selected.setQuantity(Integer.parseInt(qtyField.getText()));
            selected.setSupplier(supplierField.getText());
            selected.setPrice(Double.parseDouble(priceField.getText()));
            selected.setImportDate(importField.getText());
            selected.setLastUpdate(java.time.LocalDate.now().toString());

            stockDAO.update(selected);

            loadStock();
        }
    }

    @FXML
    public void handleDeleteStock(ActionEvent event){

        Stock selected = stockTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select stock to delete");
            alert.show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Stock");
        confirm.setHeaderText("Confirm Delete");
        confirm.setContentText("Delete stock of " + selected.getProductName() + "?");

        if(confirm.showAndWait().get() == ButtonType.OK){

            stockDAO.delete(selected);
            loadStock();
        }
    }

    // ================= SEARCH + SORT =================

    @FXML
    public void sortNameAZ(){

        FXCollections.sort(stockList,
                (a,b)->a.getProductName().compareToIgnoreCase(b.getProductName()));

        stockTable.setItems(stockList);
    }

    @FXML
    public void sortNameZA(){

        FXCollections.sort(stockList,
                (a,b)->b.getProductName().compareToIgnoreCase(a.getProductName()));

        stockTable.setItems(stockList);
    }

    @FXML
    public void sortQuantityAsc(){

        FXCollections.sort(stockList,
                (a,b)->Integer.compare(a.getQuantity(), b.getQuantity()));

        stockTable.setItems(stockList);
    }

    @FXML
    public void sortQuantityDesc(){

        FXCollections.sort(stockList,
                (a,b)->Integer.compare(b.getQuantity(), a.getQuantity()));

        stockTable.setItems(stockList);
    }

    @FXML
    public void sortNewest(){

        FXCollections.sort(stockList,
                (a,b)->b.getLastUpdate().compareTo(a.getLastUpdate()));

        stockTable.setItems(stockList);
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