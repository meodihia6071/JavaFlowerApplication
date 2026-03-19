package flowershop.controllers;

import flowershop.dao.CustomerDAO;
import flowershop.models.Customer;
import flowershop.models.User;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.utils.HibernateUtil;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

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

    private CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Customer> customerList;

    // ================= INIT =================
    @FXML
    public void initialize(){

        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCustomerId()).asObject()
        );

        colUserId.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getUser() != null
                                ? data.getValue().getUser().getUserId()
                                : 0
                ).asObject()
        );

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomerName())
        );

        colPhone.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhone())
        );

        colEmail.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail())
        );

        colPoints.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getPoints()).asObject()
        );

        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadCustomers();
        setActiveMenu("Customers");
    }

    // ================= LOAD =================
    private void loadCustomers(){
        List<Customer> list = customerDAO.getAll();
        customerList = FXCollections.observableArrayList(list);
        customerTable.setItems(customerList);
    }

    // ================= HELPER =================
    private User getUserById(int id){
        return HibernateUtil.getSessionFactory()
                .openSession()
                .get(User.class, id);
    }

    // ================= ADD =================
    @FXML
    public void handleAddCustomer(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Customer");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField userIdField = new TextField();
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField pointsField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("User ID:"),0,0);
        grid.add(userIdField,1,0);

        grid.add(new Label("Name:"),0,1);
        grid.add(nameField,1,1);

        grid.add(new Label("Phone:"),0,2);
        grid.add(phoneField,1,2);

        grid.add(new Label("Email:"),0,3);
        grid.add(emailField,1,3);

        grid.add(new Label("Points:"),0,4);
        grid.add(pointsField,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            try {
                int userId = Integer.parseInt(userIdField.getText());
                User user = getUserById(userId);

                if(user == null){
                    new Alert(Alert.AlertType.ERROR,"User not found!").show();
                    return;
                }

                Customer c = new Customer();
                c.setUser(user);
                c.setCustomerName(nameField.getText());
                c.setPhone(phoneField.getText());
                c.setEmail(emailField.getText());
                c.setPoints(Integer.parseInt(pointsField.getText()));

                customerDAO.save(c);
                loadCustomers();

            } catch (Exception e){
                new Alert(Alert.AlertType.ERROR,"Invalid input!").show();
            }
        }
    }

    // ================= EDIT =================
    @FXML
    public void handleEditCustomer(){

        Customer selected = customerTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            new Alert(Alert.AlertType.WARNING,"Select customer first!").show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Customer");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField userIdField = new TextField(
                selected.getUser() != null
                        ? String.valueOf(selected.getUser().getUserId())
                        : ""
        );

        TextField nameField = new TextField(selected.getCustomerName());
        TextField phoneField = new TextField(selected.getPhone());
        TextField emailField = new TextField(selected.getEmail());
        TextField pointsField = new TextField(String.valueOf(selected.getPoints()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("User ID:"),0,0);
        grid.add(userIdField,1,0);

        grid.add(new Label("Name:"),0,1);
        grid.add(nameField,1,1);

        grid.add(new Label("Phone:"),0,2);
        grid.add(phoneField,1,2);

        grid.add(new Label("Email:"),0,3);
        grid.add(emailField,1,3);

        grid.add(new Label("Points:"),0,4);
        grid.add(pointsField,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            try {
                int userId = Integer.parseInt(userIdField.getText());
                User user = getUserById(userId);

                if(user == null){
                    new Alert(Alert.AlertType.ERROR,"User not found!").show();
                    return;
                }

                selected.setUser(user);
                selected.setCustomerName(nameField.getText());
                selected.setPhone(phoneField.getText());
                selected.setEmail(emailField.getText());
                selected.setPoints(Integer.parseInt(pointsField.getText()));

                customerDAO.update(selected);
                loadCustomers();

            } catch (Exception e){
                new Alert(Alert.AlertType.ERROR,"Invalid input!").show();
            }
        }
    }

    // ================= DELETE =================
    @FXML
    public void handleDeleteCustomer(){

        Customer selected = customerTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            new Alert(Alert.AlertType.WARNING,"Select customer!").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        confirm.getDialogPane().getStyleClass().add("dialog-pane");
        confirm.setContentText("Delete " + selected.getCustomerName() + "?");

        if(confirm.showAndWait().get() == ButtonType.OK){
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
            if(c.getCustomerName().toLowerCase().contains(keyword)
                    || c.getPhone().contains(keyword)){
                filtered.add(c);
            }
        }

        customerTable.setItems(filtered);
    }

    // ================= SORT =================
    @FXML
    public void sortNameAZ(){
        FXCollections.sort(customerList,
                (a,b)->a.getCustomerName().compareToIgnoreCase(b.getCustomerName()));
    }

    @FXML
    public void sortNameZA(){
        FXCollections.sort(customerList,
                (a,b)->b.getCustomerName().compareToIgnoreCase(a.getCustomerName()));
    }

    @FXML
    public void sortNewest(){
        FXCollections.sort(customerList,
                (a,b) -> b.getCustomerId() - a.getCustomerId());
    }

    // ================= MENU =================
    private void setActiveMenu(String name){

        if(sidebar == null) return;

        for(Node node : sidebar.getChildren()){
            if(node instanceof Button){
                Button btn = (Button) node;
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
    @FXML public void goEmployees(){ SceneManager.switchScene("/fxml/AdminEmployees.fxml","Employees");}
    @FXML public void goReports(){ SceneManager.switchScene("/fxml/AdminReports.fxml","Reports"); }

    @FXML
    public void handleLogout(){
        SessionManager.clear();
        SceneManager.switchScene("/fxml/login.fxml","Login");
    }
}