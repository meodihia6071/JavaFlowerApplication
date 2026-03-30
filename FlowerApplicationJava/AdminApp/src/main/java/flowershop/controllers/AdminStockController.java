package flowershop.controllers;

import flowershop.dao.StockDAO;
import flowershop.models.Product;
import flowershop.models.Stock;
import flowershop.models.Supplier;
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
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.util.List;

public class AdminStockController {

    @FXML private TableView<Stock> stockTable;
    @FXML private TableColumn<Stock, Integer> colId;
    @FXML private TableColumn<Stock, String> colProduct;
    @FXML private TableColumn<Stock, Integer> colQuantity;
    @FXML private TableColumn<Stock, Double> colImportPrice;
    @FXML private TableColumn<Stock, String> colSupplier;
    @FXML private TableColumn<Stock, LocalDate> colImportDate;

    @FXML private TextField searchField;
    @FXML private VBox sidebar;
    private final NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    private StockDAO stockDAO = new StockDAO();
    private ObservableList<Stock> stockList;

    @FXML
    public void initialize(){
        try {
            colId.setCellValueFactory(new PropertyValueFactory<>("stockId"));
            colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
            colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
            colImportDate.setCellValueFactory(new PropertyValueFactory<>("importDate"));

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
            colImportPrice.setCellFactory(col -> new TableCell<>(){
                @Override
                protected void updateItem(Double value, boolean empty){
                    super.updateItem(value, empty);
                    setStyle("-fx-alignment: CENTER-RIGHT;");
                    if(empty || value == null){
                        setText(null);
                    } else {
                        setText(vnFormat.format(value) + "₫");
                    }
                }
            });

            stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            loadStock();
            setActiveMenu("Stock");
        } catch (Exception e) {
            System.out.println("Lỗi khởi tạo bảng Stock: " + e.getMessage());
        }
    }

    private void loadStock(){
        List<Stock> list = stockDAO.getAllStock();
        if (list != null) {
            stockList = FXCollections.observableArrayList(list);
            stockTable.setItems(stockList);
        }
    }

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

    private void applySafeCss(DialogPane dialogPane) {
        URL cssUrl = getClass().getResource("/css/admin-style.css");
        if (cssUrl != null) dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }

    @FXML
    public void handleAddStock(ActionEvent event){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Stock");
        applySafeCss(dialog.getDialogPane());

        TextField productIdField = new TextField();
        productIdField.setPromptText("Nhập ID Sản phẩm");

        TextField supplierIdField = new TextField();
        supplierIdField.setPromptText("Nhập ID Nhà cung cấp");

        TextField qtyField = new TextField();
        TextField importPriceField = new TextField();

        DatePicker importDatePicker = new DatePicker();
        importDatePicker.setValue(LocalDate.now());

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(12); grid.setStyle("-fx-padding:20;");

        grid.add(new Label("Product ID:"),0,0); grid.add(productIdField,1,0);
        grid.add(new Label("Supplier ID:"),0,1); grid.add(supplierIdField,1,1);
        grid.add(new Label("Quantity:"),0,2); grid.add(qtyField,1,2);
        grid.add(new Label("Import Price:"),0,3); grid.add(importPriceField,1,3);
        grid.add(new Label("Import Date:"),0,4); grid.add(importDatePicker, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){
            try {
                Stock stock = new Stock();
                Product p = new Product();
                p.setProductId(Integer.parseInt(productIdField.getText()));
                stock.setProduct(p);

                Supplier s = new Supplier();
                s.setSupplierId(Integer.parseInt(supplierIdField.getText()));
                stock.setSupplierEntity(s);

                stock.setQuantity(Integer.parseInt(qtyField.getText()));
                stock.setImportPrice(Double.parseDouble(importPriceField.getText()));
                stock.setImportDate(importDatePicker.getValue()); // ĐÃ FIX: Lấy trực tiếp Date

                stockDAO.save(stock);
                loadStock();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng nhập đúng định dạng số cho ID, Số lượng và Giá!");
                applySafeCss(alert.getDialogPane());
                alert.show();
            }
        }
    }

    @FXML
    public void handleEditStock(ActionEvent event){
        Stock selected = stockTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn dòng cần sửa!");
            applySafeCss(alert.getDialogPane());
            alert.show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Stock");
        applySafeCss(dialog.getDialogPane());

        String pId = selected.getProduct() != null ? String.valueOf(selected.getProduct().getProductId()) : "";
        String sId = selected.getSupplierEntity() != null ? String.valueOf(selected.getSupplierEntity().getSupplierId()) : "";

        TextField productIdField = new TextField(pId);
        TextField supplierIdField = new TextField(sId);
        TextField qtyField = new TextField(String.valueOf(selected.getQuantity()));
        TextField importPriceField = new TextField(String.valueOf(selected.getImportPrice()));

        // ĐÃ FIX: Dùng bảng chọn lịch thay vì gõ tay
        DatePicker importDatePicker = new DatePicker(selected.getImportDate());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        grid.add(new Label("Product ID:"),0,0); grid.add(productIdField,1,0);
        grid.add(new Label("Supplier ID:"),0,1); grid.add(supplierIdField,1,1);
        grid.add(new Label("Quantity:"),0,2); grid.add(qtyField,1,2);
        grid.add(new Label("Import Price:"),0,3); grid.add(importPriceField,1,3);
        grid.add(new Label("Import Date:"),0,4); grid.add(importDatePicker,1,4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){
            try {
                Product p = new Product();
                p.setProductId(Integer.parseInt(productIdField.getText()));
                selected.setProduct(p);

                Supplier s = new Supplier();
                s.setSupplierId(Integer.parseInt(supplierIdField.getText()));
                selected.setSupplierEntity(s);

                selected.setQuantity(Integer.parseInt(qtyField.getText()));
                selected.setImportPrice(Double.parseDouble(importPriceField.getText()));
                selected.setImportDate(importDatePicker.getValue()); // ĐÃ FIX: Lưu LocalDate

                stockDAO.update(selected);
                loadStock();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi cập nhật. Vui lòng kiểm tra lại số liệu.");
                applySafeCss(alert.getDialogPane());
                alert.show();
            }
        }
    }

    @FXML
    public void handleDeleteStock(ActionEvent event){
        Stock selected = stockTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn dòng cần xóa!");
            applySafeCss(alert.getDialogPane());
            alert.show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        applySafeCss(confirm.getDialogPane());
        confirm.setTitle("Delete Stock");
        confirm.setContentText("Bạn có chắc muốn xóa lô hàng của sản phẩm ID " + selected.getProduct().getProductId() + "?");

        if(confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK){
            stockDAO.delete(selected);
            loadStock();
        }
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
            if(s.getProductName() != null && s.getProductName().toLowerCase().contains(keyword)){
                filtered.add(s);
            }
        }
        stockTable.setItems(filtered);
    }

    @FXML public void sortNameAZ(){ FXCollections.sort(stockList, (a,b)->a.getProductName().compareToIgnoreCase(b.getProductName())); stockTable.setItems(stockList); }
    @FXML public void sortNameZA(){ FXCollections.sort(stockList, (a,b)->b.getProductName().compareToIgnoreCase(a.getProductName())); stockTable.setItems(stockList); }
    @FXML public void sortQuantityAsc(){ FXCollections.sort(stockList, (a,b)->Integer.compare(a.getQuantity(), b.getQuantity())); stockTable.setItems(stockList); }
    @FXML public void sortQuantityDesc(){ FXCollections.sort(stockList, (a,b)->Integer.compare(b.getQuantity(), a.getQuantity())); stockTable.setItems(stockList); }
    @FXML public void sortNewest(){ FXCollections.sort(stockList, (a,b)->b.getImportDate().compareTo(a.getImportDate())); stockTable.setItems(stockList); }

    @FXML public void goDashboard(ActionEvent event){ SceneManager.switchScene("/fxml/AdminDashboard.fxml","Dashboard"); }
    @FXML public void goProducts(ActionEvent event){ SceneManager.switchScene("/fxml/AdminProducts.fxml","Products"); }
    @FXML public void goCategories(ActionEvent event){ SceneManager.switchScene("/fxml/AdminCategories.fxml","Categories"); }
    @FXML public void goOrders(ActionEvent event){ SceneManager.switchScene("/fxml/AdminOrders.fxml","Orders"); }
    @FXML public void goCustomers(ActionEvent event){ SceneManager.switchScene("/fxml/AdminCustomers.fxml","Customers"); }
    @FXML public void goSuppliers(ActionEvent event){ SceneManager.switchScene("/fxml/AdminSuppliers.fxml","Suppliers"); }
    @FXML public void goStock(ActionEvent event){ SceneManager.switchScene("/fxml/AdminStock.fxml","Stock"); }
    @FXML public void goEmployees(){ SceneManager.switchScene("/fxml/AdminEmployees.fxml","Employees");}
    @FXML public void goReports(ActionEvent event){ SceneManager.switchScene("/fxml/AdminReports.fxml","Reports"); }
    @FXML public void handleLogout(ActionEvent event){ SessionManager.clear(); SceneManager.switchScene("/fxml/login.fxml","Login"); }
}