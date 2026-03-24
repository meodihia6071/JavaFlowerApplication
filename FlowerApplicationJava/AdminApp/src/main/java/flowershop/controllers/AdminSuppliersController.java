package flowershop.controllers;

import flowershop.models.Supplier;
import flowershop.dao.SupplierDAO;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.TableCell;
import javafx.scene.layout.GridPane;

public class AdminSuppliersController {
    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier, Integer> colId;

    @FXML
    private TableColumn<Supplier, String> colName;

    @FXML
    private TableColumn<Supplier, String> colPhone;

    @FXML
    private TableColumn<Supplier, String> colEmail;

    @FXML
    private TableColumn<Supplier, String> colAddress;

    // Sửa LocalDate thành String cho khớp với Model Supplier
    @FXML
    private TableColumn<Supplier, String> colCreatedDate;

    @FXML
    private TableColumn<Supplier, String> colStatus;

    @FXML
    private TextField searchField;

    private SupplierDAO supplierDAO = new SupplierDAO();

    private ObservableList<Supplier> supplierList;

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        // Sửa "supplierName" thành "name" cho khớp với biến trong Model
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(column -> new TableCell<Supplier, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Circle circle = new Circle(5);
                    if (status.equalsIgnoreCase("Active")) {
                        circle.setFill(Color.GREEN);
                        setText(" Active");
                    } else {
                        circle.setFill(Color.RED);
                        setText(" Inactive");
                    }
                    setGraphic(circle);
                    setContentDisplay(ContentDisplay.LEFT);
                }
            }
        });

        supplierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadSuppliers();
    }

    private void loadSuppliers(){
        List<Supplier> list = supplierDAO.getAllSuppliers();
        supplierList = FXCollections.observableArrayList(list);
        supplierTable.setItems(supplierList);
    }

    // ================= HÀM TIỆN ÍCH BẮT LỖI CSS AN TOÀN =================
    private void applySafeCss(DialogPane dialogPane) {
        URL cssUrl = getClass().getResource("/css/admin-style.css");
        if (cssUrl != null) {
            dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        }
        dialogPane.getStyleClass().add("dialog-pane");
    }

    // ================= NAVIGATION =================
    @FXML void goDashboard(ActionEvent e){ SceneManager.switchScene("/fxml/AdminDashboard.fxml","Dashboard"); }
    @FXML void goProducts(ActionEvent e){ SceneManager.switchScene("/fxml/AdminProducts.fxml","Products"); }
    @FXML void goCategories(ActionEvent e){ SceneManager.switchScene("/fxml/AdminCategories.fxml","Categories"); }
    @FXML void goOrders(ActionEvent e){ SceneManager.switchScene("/fxml/AdminOrders.fxml","Orders"); }
    @FXML void goCustomers(ActionEvent e){ SceneManager.switchScene("/fxml/AdminCustomers.fxml","Customers"); }
    @FXML void goStock(ActionEvent e){ SceneManager.switchScene("/fxml/AdminStock.fxml","Stock"); }
    @FXML public void goEmployees(){ SceneManager.switchScene("/fxml/AdminEmployees.fxml","Employees");}
    @FXML void goReports(ActionEvent e){ SceneManager.switchScene("/fxml/AdminReports.fxml","Reports"); }
    @FXML void handleLogout(ActionEvent e){ SessionManager.clear(); SceneManager.switchScene("/fxml/login.fxml","Login"); }

    // ================= ADD =================
    @FXML
    public void handleAddSupplier(ActionEvent event){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Supplier");

        applySafeCss(dialog.getDialogPane()); // Dùng hàm an toàn

        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField addressField = new TextField();
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active","Inactive");
        statusBox.setValue("Active");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setStyle("-fx-padding:20;");

        grid.add(new Label("Supplier Name:"),0,0); grid.add(nameField,1,0);
        grid.add(new Label("Phone:"),0,1); grid.add(phoneField,1,1);
        grid.add(new Label("Email:"),0,2); grid.add(emailField,1,2);
        grid.add(new Label("Address:"),0,3); grid.add(addressField,1,3);
        grid.add(new Label("Status:"),0,4); grid.add(statusBox,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            Supplier s = new Supplier();

            s.setName(nameField.getText()); // Đổi setSupplierName -> setName
            s.setPhone(phoneField.getText());
            s.setEmail(emailField.getText());
            s.setAddress(addressField.getText());
            s.setStatus(statusBox.getValue());
            s.setCreatedDate(LocalDate.now().toString()); // Ép kiểu về String cho chuẩn

            supplierDAO.save(s);
            loadSuppliers();
        }
    }

    // ================= EDIT =================
    @FXML
    public void handleEditSupplier(ActionEvent event){

        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select supplier first!");
            applySafeCss(alert.getDialogPane());
            alert.show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Supplier");

        applySafeCss(dialog.getDialogPane());

        Label nameLabel = new Label("Supplier Name:");
        TextField nameField = new TextField(selected.getName()); // Đổi getSupplierName -> getName

        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField(selected.getPhone());

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(selected.getEmail());

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField(selected.getAddress());

        Label statusLabel = new Label("Status:");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active","Inactive");
        statusBox.setValue(selected.getStatus());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(nameLabel,0,0); grid.add(nameField,1,0);
        grid.add(phoneLabel,0,1); grid.add(phoneField,1,1);
        grid.add(emailLabel,0,2); grid.add(emailField,1,2);
        grid.add(addressLabel,0,3); grid.add(addressField,1,3);
        grid.add(statusLabel,0,4); grid.add(statusBox,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            selected.setName(nameField.getText()); // Đổi setSupplierName -> setName
            selected.setPhone(phoneField.getText());
            selected.setEmail(emailField.getText());
            selected.setAddress(addressField.getText());
            selected.setStatus(statusBox.getValue());

            supplierDAO.update(selected);
            loadSuppliers();
        }
    }

    // ================= DELETE =================
    @FXML
    void handleDeleteSupplier(ActionEvent e){

        Supplier s = supplierTable.getSelectionModel().getSelectedItem();

        if(s == null){
            showAlert("Please select a supplier");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        applySafeCss(confirm.getDialogPane());
        confirm.setHeaderText("Delete Supplier?");
        confirm.setContentText("Are you sure you want to delete this supplier?");

        Optional<ButtonType> result = confirm.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK){
            supplierDAO.delete(s);
            loadSuppliers();
        }
    }

    // ================= SEARCH =================
    @FXML
    void handleSearch(ActionEvent e){

        String keyword = searchField.getText().toLowerCase();

        if(keyword.isEmpty()){
            supplierTable.setItems(supplierList);
            return;
        }

        supplierTable.setItems(
                supplierList.filtered(
                        s -> s.getName().toLowerCase().contains(keyword) // Đổi getSupplierName -> getName
                )
        );
    }

    // ================= SORT =================
    @FXML
    void sortNameAZ(){
        FXCollections.sort(
                supplierList,
                Comparator.comparing(Supplier::getName) // Đổi getSupplierName -> getName
        );
    }

    @FXML
    void sortNameZA(){
        FXCollections.sort(
                supplierList,
                Comparator.comparing(Supplier::getName).reversed() // Đổi getSupplierName -> getName
        );
    }

    @FXML
    void sortNewest(){
        FXCollections.sort(
                supplierList,
                Comparator.comparing(Supplier::getSupplierId).reversed()
        );
    }

    @FXML
    void sortOldest(){
        FXCollections.sort(
                supplierList,
                Comparator.comparing(Supplier::getSupplierId)
        );
    }

    // ================= ALERT =================
    private void showAlert(String text){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(text);
        a.showAndWait();
    }
}