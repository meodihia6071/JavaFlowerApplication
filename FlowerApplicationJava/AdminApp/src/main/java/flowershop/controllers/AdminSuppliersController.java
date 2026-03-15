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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AdminSuppliersController {

    @FXML
    private TableView<Supplier> supplierTable;

    @FXML
    private TableColumn<Supplier,Integer> colId;

    @FXML
    private TableColumn<Supplier,String> colName;

    @FXML
    private TableColumn<Supplier,String> colPhone;

    @FXML
    private TableColumn<Supplier,String> colAddress;

    @FXML
    private TextField searchField;

    private SupplierDAO supplierDAO = new SupplierDAO();

    private ObservableList<Supplier> supplierList;

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        supplierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadSuppliers();
    }

    private void loadSuppliers(){

        List<Supplier> list = supplierDAO.getAllSuppliers();

        supplierList = FXCollections.observableArrayList(list);

        supplierTable.setItems(supplierList);
    }

    // ================= NAVIGATION =================

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

    // ================= ADD =================

    @FXML
    void handleAddSupplier(ActionEvent e){

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setHeaderText("Enter Supplier Name");

        Optional<String> name = nameDialog.showAndWait();
        if(name.isEmpty()) return;

        TextInputDialog phoneDialog = new TextInputDialog();
        phoneDialog.setHeaderText("Enter Phone");

        Optional<String> phone = phoneDialog.showAndWait();
        if(phone.isEmpty()) return;

        TextInputDialog addressDialog = new TextInputDialog();
        addressDialog.setHeaderText("Enter Address");

        Optional<String> address = addressDialog.showAndWait();
        if(address.isEmpty()) return;

        Supplier s = new Supplier();

        s.setSupplierName(name.get());
        s.setPhone(phone.get());
        s.setAddress(address.get());

        supplierDAO.save(s);

        loadSuppliers();
    }

    // ================= EDIT =================

    @FXML
    void handleEditSupplier(ActionEvent e){

        Supplier s = supplierTable.getSelectionModel().getSelectedItem();

        if(s == null){
            showAlert("Please select a supplier");
            return;
        }

        TextInputDialog nameDialog = new TextInputDialog(s.getSupplierName());
        nameDialog.setHeaderText("Edit Supplier Name");

        Optional<String> name = nameDialog.showAndWait();
        if(name.isEmpty()) return;

        TextInputDialog phoneDialog = new TextInputDialog(s.getPhone());
        phoneDialog.setHeaderText("Edit Phone");

        Optional<String> phone = phoneDialog.showAndWait();
        if(phone.isEmpty()) return;

        TextInputDialog addressDialog = new TextInputDialog(s.getAddress());
        addressDialog.setHeaderText("Edit Address");

        Optional<String> address = addressDialog.showAndWait();
        if(address.isEmpty()) return;

        s.setSupplierName(name.get());
        s.setPhone(phone.get());
        s.setAddress(address.get());

        supplierDAO.update(s);

        loadSuppliers();
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
                        s -> s.getSupplierName().toLowerCase().contains(keyword)
                )
        );
    }

    // ================= SORT =================

    @FXML
    void sortNameAZ(){
        FXCollections.sort(
                supplierList,
                Comparator.comparing(Supplier::getSupplierName)
        );
    }

    @FXML
    void sortNameZA(){
        FXCollections.sort(
                supplierList,
                Comparator.comparing(Supplier::getSupplierName).reversed()
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