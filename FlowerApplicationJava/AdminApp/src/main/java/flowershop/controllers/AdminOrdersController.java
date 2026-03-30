package flowershop.controllers;

import flowershop.dao.OrderDAO;
import flowershop.dao.OrderDetailDAO;
import flowershop.models.Order;
import flowershop.models.OrderDetail;

import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.reflect.Field;
import java.util.List;

public class AdminOrdersController {

    // ===== TABLE ORDER =====
    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, String> colOrderId;
    @FXML private TableColumn<Order, String> colCustomer;
    @FXML private TableColumn<Order, String> colDate;
    @FXML private TableColumn<Order, String> colTotal;
    @FXML private TableColumn<Order, String> colStatus;

    // ===== TABLE DETAIL =====
    @FXML private TableView<OrderDetail> detailTable;
    @FXML private TableColumn<OrderDetail, String> colProduct;
    @FXML private TableColumn<OrderDetail, String> colQty;
    @FXML private TableColumn<OrderDetail, String> colPrice;
    @FXML private TableColumn<OrderDetail, String> colSubTotal;

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO detailDAO = new OrderDetailDAO();

    // ===== INIT =====
    @FXML
    public void initialize() {
        initOrderTable();
        initDetailTable();
        loadOrders();
        handleRowClick();
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        detailTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ================= ORDER TABLE =================
    private void initOrderTable(){

        colOrderId.setCellValueFactory(cell ->
                new SimpleStringProperty(getField(cell.getValue(), "orderId"))
        );

        colCustomer.setCellValueFactory(cell -> {
            Object customer = getObject(cell.getValue(), "customer");
            if(customer == null) return new SimpleStringProperty("N/A");

            return new SimpleStringProperty(getField(customer, "customerName"));
        });

        colDate.setCellValueFactory(cell ->
                new SimpleStringProperty(getField(cell.getValue(), "orderDate"))
        );

        colTotal.setCellValueFactory(cell ->
                new SimpleStringProperty(getField(cell.getValue(), "total"))
        );

        colStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(getField(cell.getValue(), "status"))
        );
    }

    // ================= DETAIL TABLE =================
    private void initDetailTable(){

        colProduct.setCellValueFactory(cell -> {
            Object product = getObject(cell.getValue(), "product");
            if(product == null) return new SimpleStringProperty("N/A");

            return new SimpleStringProperty(getField(product, "productName"));
        });

        colQty.setCellValueFactory(cell ->
                new SimpleStringProperty(getField(cell.getValue(), "quantity"))
        );

        colPrice.setCellValueFactory(cell ->
                new SimpleStringProperty(getField(cell.getValue(), "price"))
        );

        colSubTotal.setCellValueFactory(cell -> {
            try {
                Object priceObj = getRaw(cell.getValue(), "price");
                Object qtyObj = getRaw(cell.getValue(), "quantity");

                double price = Double.parseDouble(priceObj.toString());
                int qty = Integer.parseInt(qtyObj.toString());

                return new SimpleStringProperty(String.valueOf(price * qty));

            } catch (Exception e){
                return new SimpleStringProperty("Error");
            }
        });
    }

    // ================= LOAD DATA =================
    private void loadOrders(){
        List<Order> list = orderDAO.getAllOrders();
        orderTable.setItems(FXCollections.observableArrayList(list));
    }

    private void loadOrderDetails(Order order){
        List<OrderDetail> list = detailDAO.getByOrderId(
                Integer.parseInt(getField(order, "orderId"))
        );

        detailTable.setItems(FXCollections.observableArrayList(list));
    }

    // ================= CLICK EVENT =================
    private void handleRowClick(){
        orderTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if(newVal != null){
                        loadOrderDetails(newVal);
                    }
                }
        );
    }

    // ================= REFLECTION UTILS =================

    private String getField(Object obj, String fieldName){
        try{
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object val = f.get(obj);
            return val != null ? val.toString() : "N/A";
        }catch(Exception e){
            return "Err";
        }
    }

    private Object getObject(Object obj, String fieldName){
        try{
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        }catch(Exception e){
            return null;
        }
    }

    private Object getRaw(Object obj, String fieldName){
        try{
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        }catch(Exception e){
            return null;
        }
    }

    @FXML public void goDashboard(){ SceneManager.switchScene("/fxml/AdminDashboard.fxml","Dashboard"); }
    @FXML public void goProducts(){ SceneManager.switchScene("/fxml/AdminProducts.fxml","Products"); }
    @FXML public void goCategories(){ SceneManager.switchScene("/fxml/AdminCategories.fxml","Categories"); }
    @FXML public void goOrders(){ SceneManager.switchScene("/fxml/AdminOrders.fxml","Orders"); }
    @FXML public void goCustomers(){ SceneManager.switchScene("/fxml/AdminCustomers.fxml","Customers"); }
    @FXML public void goSuppliers(){ SceneManager.switchScene("/fxml/AdminSuppliers.fxml","Suppliers"); }
    @FXML public void goStock(){ SceneManager.switchScene("/fxml/AdminStock.fxml","Stock"); }
    @FXML public void goReports(){ SceneManager.switchScene("/fxml/AdminReports.fxml","Reports"); }

    @FXML
    public void handleLogout(){
        SessionManager.clear();
        SceneManager.switchScene("/fxml/login.fxml","Login");
    }
}