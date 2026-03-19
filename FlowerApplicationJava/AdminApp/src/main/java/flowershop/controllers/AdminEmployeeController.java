package flowershop.controllers;

import flowershop.dao.EmployeeDAO;
import flowershop.models.Employee;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import java.util.List;

public class AdminEmployeeController {

    @FXML private TableView<Employee> employeeTable;

    @FXML private TableColumn<Employee, Integer> colId;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TableColumn<Employee, String> colRole;
    @FXML private TableColumn<Employee, Double> colSalary;
    @FXML private TableColumn<Employee, String> colStatus;

    @FXML private TextField searchField;
    @FXML private VBox sidebar;

    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private ObservableList<Employee> employeeList;

    // ================= INIT =================

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("employee_id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadEmployees();
        setActiveMenu("Employees");
    }

    // ================= LOAD =================

    private void loadEmployees(){
        List<Employee> list = employeeDAO.getAllEmployees();
        employeeList = FXCollections.observableArrayList(list);
        employeeTable.setItems(employeeList);
    }

    // ================= ADD =================

    @FXML
    public void handleAddEmployee(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Employee");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField roleField = new TextField();
        TextField salaryField = new TextField();

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Active", "Inactive");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        grid.add(new Label("Name:"),0,0);
        grid.add(nameField,1,0);

        grid.add(new Label("Email:"),0,1);
        grid.add(emailField,1,1);

        grid.add(new Label("Phone:"),0,2);
        grid.add(phoneField,1,2);

        grid.add(new Label("Role:"),0,3);
        grid.add(roleField,1,3);

        grid.add(new Label("Salary:"),0,4);
        grid.add(salaryField,1,4);

        grid.add(new Label("Status:"),0,5);
        grid.add(cbStatus,1,5);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            Employee e = new Employee();

            e.setName(nameField.getText());
            e.setEmail(emailField.getText());
            e.setPhone(phoneField.getText());
            e.setRole(roleField.getText());
            e.setSalary(Double.parseDouble(salaryField.getText()));
            e.setStatus(cbStatus.getValue());

            employeeDAO.save(e);
            loadEmployees();
        }
    }

    // ================= EDIT =================

    @FXML
    public void handleEditEmployee(){

        Employee selected = employeeTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            new Alert(Alert.AlertType.WARNING,"Select employee first!").show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField nameField = new TextField(selected.getName());
        TextField emailField = new TextField(selected.getEmail());
        TextField phoneField = new TextField(selected.getPhone());
        TextField roleField = new TextField(selected.getRole());
        TextField salaryField = new TextField(String.valueOf(selected.getSalary()));

        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("Active", "Inactive");
        cbStatus.setValue(selected.getStatus());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Name:"),0,0);
        grid.add(nameField,1,0);

        grid.add(new Label("Email:"),0,1);
        grid.add(emailField,1,1);

        grid.add(new Label("Phone:"),0,2);
        grid.add(phoneField,1,2);

        grid.add(new Label("Role:"),0,3);
        grid.add(roleField,1,3);

        grid.add(new Label("Salary:"),0,4);
        grid.add(salaryField,1,4);

        grid.add(new Label("Status:"),0,5);
        grid.add(cbStatus,1,5);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            selected.setName(nameField.getText());
            selected.setEmail(emailField.getText());
            selected.setPhone(phoneField.getText());
            selected.setRole(roleField.getText());
            selected.setSalary(Double.parseDouble(salaryField.getText()));
            selected.setStatus(cbStatus.getValue());

            employeeDAO.update(selected);
            loadEmployees();
        }
    }

    // ================= DELETE =================

    @FXML
    public void handleDeleteEmployee(){

        Employee selected = employeeTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            new Alert(Alert.AlertType.WARNING,"Select employee!").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        confirm.getDialogPane().getStyleClass().add("dialog-pane");
        confirm.setContentText("Delete " + selected.getName() + "?");

        if(confirm.showAndWait().get() == ButtonType.OK){
            employeeDAO.delete(selected);
            loadEmployees();
        }
    }

    // ================= SEARCH =================

    @FXML
    public void handleSearch(){

        String keyword = searchField.getText().toLowerCase();

        if(keyword.isEmpty()){
            employeeTable.setItems(employeeList);
            return;
        }

        ObservableList<Employee> filtered = FXCollections.observableArrayList();

        for(Employee e : employeeList){
            if(e.getName().toLowerCase().contains(keyword)
                    || e.getEmail().toLowerCase().contains(keyword)){
                filtered.add(e);
            }
        }

        employeeTable.setItems(filtered);
    }

    // ================= SORT =================

    @FXML
    public void sortNameAZ(){
        FXCollections.sort(employeeList,
                (a,b)->a.getName().compareToIgnoreCase(b.getName()));
    }

    @FXML
    public void sortNameZA(){
        FXCollections.sort(employeeList,
                (a,b)->b.getName().compareToIgnoreCase(a.getName()));
    }

    @FXML
    public void sortSalaryAsc(){
        FXCollections.sort(employeeList,
                (a,b)->Double.compare(a.getSalary(), b.getSalary()));
    }

    @FXML
    public void sortSalaryDesc(){
        FXCollections.sort(employeeList,
                (a,b)->Double.compare(b.getSalary(), a.getSalary()));
    }

    @FXML
    public void sortRole(){
        FXCollections.sort(employeeList,
                (a,b)->a.getRole().compareToIgnoreCase(b.getRole()));
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