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

import java.time.LocalDate;
import java.util.List;
import javafx.scene.layout.GridPane;

import java.text.NumberFormat;
import java.util.Locale;

public class AdminStockController {

    @FXML
    private TableView<Stock> stockTable;

    @FXML
    private TableColumn<Stock, Integer> colId;

    @FXML
    private TableColumn<Stock, String> colProduct;

    @FXML
    private TableColumn<Stock, Integer> colQuantity;

    @FXML
    private TableColumn<Stock, Double> colImportPrice;

    @FXML
    private TableColumn<Stock, Double> colSellPrice;

    @FXML
    private TableColumn<Stock, String> colSupplier;

    @FXML
    private TableColumn<Stock, String> colImportDate;

    @FXML
    private TextField searchField;

    @FXML
    private VBox sidebar;

    private StockDAO stockDAO = new StockDAO();

    private ObservableList<Stock> stockList;

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("stockId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQuantity.setCellFactory(column -> new TableCell<Stock, Integer>() {
            @Override
            protected void updateItem(Integer value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(value.toString());

                    if (value < 10) {
                        setStyle("-fx-text-fill:red; -fx-font-weight:bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        colImportPrice.setCellValueFactory(new PropertyValueFactory<>("importPrice"));
        colSellPrice.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        colImportPrice.setCellFactory(column -> new TableCell<Stock, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                setStyle("-fx-alignment: CENTER-RIGHT;");

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("$" + String.format("%.0f", value));
                }
            }
        });
        colSellPrice.setCellFactory(column -> new TableCell<Stock, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                setStyle("-fx-alignment: CENTER-RIGHT;");

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("$" + String.format("%.0f", value));
                }
            }
        });

        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colImportDate.setCellValueFactory(new PropertyValueFactory<>("importDate"));

        stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadStock();
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

    // ================= ADD STOCK =================

    @FXML
    public void handleAddStock(ActionEvent event){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Stock");

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField nameField = new TextField();
        TextField qtyField = new TextField();
        TextField supplierField = new TextField();
        TextField importPriceField = new TextField();
        TextField sellPriceField = new TextField();
        DatePicker importDatePicker = new DatePicker();
        importDatePicker.setValue(LocalDate.now());
        importDatePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date.isAfter(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setStyle("-fx-padding:20;");

        grid.add(new Label("Product:"),0,0);
        grid.add(nameField,1,0);

        grid.add(new Label("Quantity:"),0,1);
        grid.add(qtyField,1,1);

        grid.add(new Label("Supplier:"),0,2);
        grid.add(supplierField,1,2);

        grid.add(new Label("Import Price:"),0,3);
        grid.add(importPriceField,1,3);

        grid.add(new Label("Sell Price:"),0,4);
        grid.add(sellPriceField,1,4);

        grid.add(new Label("Import Date:"),0,5);
        grid.add(importDatePicker, 1, 5);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            Stock stock = new Stock();

            stock.setProductName(nameField.getText());
            stock.setQuantity(Integer.parseInt(qtyField.getText()));
            stock.setSupplier(supplierField.getText());
            stock.setImportPrice(Double.parseDouble(importPriceField.getText()));
            stock.setSellPrice(Double.parseDouble(sellPriceField.getText()));
            stock.setImportDate(importDatePicker.getValue().toString());

            stockDAO.save(stock);

            loadStock();
        }
    }

    // ================= EDIT =================

    @FXML
    public void handleEditStock(ActionEvent event){

        Stock selected = stockTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select stock first!");

            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/css/admin-style.css").toExternalForm()
            );

            alert.getDialogPane().getStyleClass().add("dialog-pane");

            alert.show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Stock");

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        Label nameLabel = new Label("Product:");
        TextField nameField = new TextField(selected.getProductName());

        Label qtyLabel = new Label("Quantity:");
        TextField qtyField = new TextField(String.valueOf(selected.getQuantity()));

        Label supplierLabel = new Label("Supplier:");
        TextField supplierField = new TextField(selected.getSupplier());

        Label importPriceLabel = new Label("Import Price:");
        TextField importPriceField = new TextField(String.valueOf(selected.getImportPrice()));

        Label sellPriceLabel = new Label("Sell Price:");
        TextField sellPriceField = new TextField(String.valueOf(selected.getSellPrice()));

        Label importDateLabel = new Label("Import Date:");
        TextField importDateField = new TextField(selected.getImportDate());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(nameLabel,0,0);
        grid.add(nameField,1,0);

        grid.add(qtyLabel,0,1);
        grid.add(qtyField,1,1);

        grid.add(supplierLabel,0,2);
        grid.add(supplierField,1,2);

        grid.add(importPriceLabel,0,3);
        grid.add(importPriceField,1,3);

        grid.add(sellPriceLabel,0,4);
        grid.add(sellPriceField,1,4);

        grid.add(importDateLabel,0,5);
        grid.add(importDateField,1,5);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            selected.setProductName(nameField.getText());
            selected.setQuantity(Integer.parseInt(qtyField.getText()));
            selected.setSupplier(supplierField.getText());
            selected.setImportPrice(Double.parseDouble(importPriceField.getText()));
            selected.setSellPrice(Double.parseDouble(sellPriceField.getText()));
            selected.setImportDate(importDateField.getText());

            stockDAO.update(selected);

            loadStock();
        }
    }

    // ================= DELETE =================

    @FXML
    public void handleDeleteStock(ActionEvent event){

        Stock selected = stockTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select stock to delete");
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/css/admin-style.css").toExternalForm()
            );

            alert.getDialogPane().getStyleClass().add("dialog-pane");
            alert.show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        confirm.getDialogPane().getStyleClass().add("dialog-pane");
        confirm.setTitle("Delete Stock");
        confirm.setContentText("Delete " + selected.getProductName() + "?");

        if(confirm.showAndWait().get() == ButtonType.OK){

            stockDAO.delete(selected);
            loadStock();
        }
    }

    // ================= SEARCH =================

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

    // ================= SORT =================

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
                (a,b)->b.getImportDate().compareTo(a.getImportDate()));

        stockTable.setItems(stockList);
    }
}