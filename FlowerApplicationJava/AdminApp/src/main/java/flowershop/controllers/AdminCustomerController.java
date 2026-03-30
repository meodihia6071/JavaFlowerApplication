package flowershop.controllers;

import flowershop.dao.CustomerDAO;
import flowershop.models.Customer;
import flowershop.models.User;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.utils.HibernateUtil;

import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;

public class AdminCustomerController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, Integer> colUserId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, Integer> colPoints;

    @FXML private TextField searchField;
    @FXML private VBox sidebar;

    // Các nút bấm để gắn hiệu ứng (Nếu FXML chưa có fx:id thì cứ kệ nó, code đã bọc null an toàn)
    @FXML private Button btnAdd, btnEdit, btnDelete, btnSearch, btnLogout;

    private CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Customer> customerList;

    // ================= INIT =================
    @FXML
    public void initialize(){

        // 1. Setup Cột cho Bảng
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCustomerId()).asObject());
        colUserId.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().getUser() != null ? data.getValue().getUser().getUserId() : 0).asObject()
        );
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCustomerName()));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        colPoints.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPoints()).asObject());

        if (colEmail != null) {
            colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        }

        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 2. Tải dữ liệu & Kích hoạt Menu
        loadCustomers();
        setActiveMenu("Customers");

        // 3. THÊM HIỆU ỨNG HOVER CHO CÁC NÚT (Bổ sung phần còn thiếu)
        if (btnAdd != null) addSmoothHoverEffect(btnAdd);
        if (btnEdit != null) addSmoothHoverEffect(btnEdit);
        if (btnDelete != null) addSmoothHoverEffect(btnDelete);
        if (btnSearch != null) addSmoothHoverEffect(btnSearch);
        if (btnLogout != null) addSmoothHoverEffect(btnLogout);
    }

    // ================= HIỆU ỨNG NHÚN NHẢY CHO NÚT =================
    private void addSmoothHoverEffect(Button btn) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), btn);
        scaleIn.setToX(1.05); // Phóng to 5%
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.3), btn);
        scaleOut.setToX(1.0); // Trả về bình thường
        scaleOut.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            scaleOut.stop();
            scaleIn.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            scaleIn.stop();
            scaleOut.playFromStart();
        });
    }

    // ================= LOAD =================
    private void loadCustomers(){
        List<Customer> list = customerDAO.getAll();
        customerList = FXCollections.observableArrayList(list);
        customerTable.setItems(customerList);
    }

    // ================= HÀM TIỆN ÍCH =================
    private User getUserById(int id){
        return HibernateUtil.getSessionFactory().openSession().get(User.class, id);
    }

    private void applySafeCss(DialogPane dialogPane) {
        URL cssUrl = getClass().getResource("/css/admin-style.css");
        if (cssUrl != null) {
            dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        }
        dialogPane.getStyleClass().add("dialog-pane");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        applySafeCss(alert.getDialogPane());
        alert.show();
    }

    // ================= ADD =================
    @FXML
    public void handleAddCustomer(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");
        applySafeCss(dialog.getDialogPane());

        TextField userIdField = new TextField();
        userIdField.setPromptText("Để trống nếu là khách vãng lai");
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField pointsField = new TextField("0"); // Mặc định 0 điểm

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        grid.add(new Label("User ID:"),0,0); grid.add(userIdField,1,0);
        grid.add(new Label("Name:"),0,1); grid.add(nameField,1,1);
        grid.add(new Label("Phone:"),0,2); grid.add(phoneField,1,2);
        grid.add(new Label("Email:"),0,3); grid.add(emailField,1,3);
        grid.add(new Label("Points:"),0,4); grid.add(pointsField,1,4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){
            try {
                Customer c = new Customer();

                // Xử lý an toàn cho User ID
                String uIdStr = userIdField.getText().trim();
                if (!uIdStr.isEmpty()) {
                    int userId = Integer.parseInt(uIdStr);
                    User user = getUserById(userId);
                    if(user == null){
                        showAlert(Alert.AlertType.ERROR, "Không tìm thấy User ID này trong hệ thống!");
                        return;
                    }
                    c.setUser(user);
                } else {
                    c.setUser(null); // Khách vãng lai
                }

                c.setCustomerName(nameField.getText());
                c.setPhone(phoneField.getText());
                c.setEmail(emailField.getText());
                c.setPoints(Integer.parseInt(pointsField.getText()));

                customerDAO.save(c);
                loadCustomers();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "ID hoặc Điểm phải là số!");
            } catch (Exception e){
                showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống: " + e.getMessage());
            }
        }
    }

    // ================= EDIT =================
    @FXML
    public void handleEditCustomer(){

        Customer selected = customerTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn khách hàng cần sửa!");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer");
        applySafeCss(dialog.getDialogPane());

        TextField userIdField = new TextField(selected.getUser() != null ? String.valueOf(selected.getUser().getUserId()) : "");
        TextField nameField = new TextField(selected.getCustomerName());
        TextField phoneField = new TextField(selected.getPhone());
        TextField emailField = new TextField(selected.getEmail());
        TextField pointsField = new TextField(String.valueOf(selected.getPoints()));

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        grid.add(new Label("User ID:"),0,0); grid.add(userIdField,1,0);
        grid.add(new Label("Name:"),0,1); grid.add(nameField,1,1);
        grid.add(new Label("Phone:"),0,2); grid.add(phoneField,1,2);
        grid.add(new Label("Email:"),0,3); grid.add(emailField,1,3);
        grid.add(new Label("Points:"),0,4); grid.add(pointsField,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){
            try {
                String uIdStr = userIdField.getText().trim();
                if (!uIdStr.isEmpty()) {
                    int userId = Integer.parseInt(uIdStr);
                    User user = getUserById(userId);
                    if(user == null){
                        showAlert(Alert.AlertType.ERROR, "Không tìm thấy User ID này trong hệ thống!");
                        return;
                    }
                    selected.setUser(user);
                } else {
                    selected.setUser(null);
                }

                selected.setCustomerName(nameField.getText());
                selected.setPhone(phoneField.getText());
                selected.setEmail(emailField.getText());
                selected.setPoints(Integer.parseInt(pointsField.getText()));

                customerDAO.update(selected);
                loadCustomers();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "ID hoặc Điểm phải là số!");
            } catch (Exception e){
                showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống: " + e.getMessage());
            }
        }
    }

    // ================= DELETE =================
    @FXML
    public void handleDeleteCustomer(){

        Customer selected = customerTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            showAlert(Alert.AlertType.WARNING, "Vui lòng chọn khách hàng cần xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa khách hàng " + selected.getCustomerName() + "?");
        applySafeCss(confirm.getDialogPane());

        if(confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK){
            customerDAO.delete(selected);
            loadCustomers();
        }
    }

    // ================= SEARCH =================
    @FXML
    public void handleSearch(){

        String keyword = searchField.getText().toLowerCase();

        if(keyword.isEmpty()){
            customerTable.setItems(customerList);
            return;
        }

        ObservableList<Customer> filtered = FXCollections.observableArrayList();

        for(Customer c : customerList){
            if((c.getCustomerName() != null && c.getCustomerName().toLowerCase().contains(keyword))
                    || (c.getPhone() != null && c.getPhone().contains(keyword))){
                filtered.add(c);
            }
        }

        customerTable.setItems(filtered);
    }

    // ================= SORT =================
    @FXML
    public void sortNameAZ(){
        FXCollections.sort(customerList, (a,b)->a.getCustomerName().compareToIgnoreCase(b.getCustomerName()));
    }

    @FXML
    public void sortNameZA(){
        FXCollections.sort(customerList, (a,b)->b.getCustomerName().compareToIgnoreCase(a.getCustomerName()));
    }

    @FXML
    public void sortNewest(){
        FXCollections.sort(customerList, (a,b) -> b.getCustomerId() - a.getCustomerId());
    }

    // ================= MENU (Bổ sung gán hiệu ứng cho Sidebar luôn) =================
    private void setActiveMenu(String name){
        if(sidebar == null) return;
        for(Node node : sidebar.getChildren()){
            if(node instanceof Button){
                Button btn = (Button) node;

                // Tiện tay gán luôn hiệu ứng hover cho tất cả nút trong Sidebar
                addSmoothHoverEffect(btn);

                btn.getStyleClass().remove("menu-active");
                if(btn.getText().toLowerCase().contains(name.toLowerCase())){
                    btn.getStyleClass().add("menu-active");
                }
            }
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