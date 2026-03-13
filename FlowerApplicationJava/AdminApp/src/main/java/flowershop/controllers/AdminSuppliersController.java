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

    @FXML
    private ComboBox<String> filterBox;

    private SupplierDAO supplierDAO = new SupplierDAO();

    private ObservableList<Supplier> supplierList;

    private boolean sortAsc = true;

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadSuppliers();

        filterBox.getItems().addAll(
                "All",
                "A-M",
                "N-Z"
        );

        filterBox.setValue("All");
    }

    private void loadSuppliers(){

        List<Supplier> list = supplierDAO.getAllSuppliers();

        supplierList = FXCollections.observableArrayList(list);

        supplierTable.setItems(supplierList);
    }

    // NAVIGATION
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

    // ADD
    @FXML
    void handleAddSupplier(ActionEvent e){

        TextInputDialog name = new TextInputDialog();
        name.setHeaderText("Supplier Name");
        Optional<String> nameResult = name.showAndWait();

        if(nameResult.isEmpty()) return;

        TextInputDialog phone = new TextInputDialog();
        phone.setHeaderText("Phone");
        Optional<String> phoneResult = phone.showAndWait();

        if(phoneResult.isEmpty()) return;

        TextInputDialog address = new TextInputDialog();
        address.setHeaderText("Address");
        Optional<String> addressResult = address.showAndWait();

        if(addressResult.isEmpty()) return;

        Supplier s = new Supplier();

        s.setSupplierName(nameResult.get());
        s.setPhone(phoneResult.get());
        s.setAddress(addressResult.get());

        supplierDAO.save(s);

        loadSuppliers();
    }

    // EDIT
    @FXML
    void handleEditSupplier(ActionEvent e){

        Supplier s = supplierTable.getSelectionModel().getSelectedItem();

        if(s == null){
            showAlert("Please select supplier");
            return;
        }

        TextInputDialog name = new TextInputDialog(s.getSupplierName());
        name.setHeaderText("Edit Supplier Name");
        Optional<String> nameResult = name.showAndWait();

        if(nameResult.isEmpty()) return;

        TextInputDialog phone = new TextInputDialog(s.getPhone());
        phone.setHeaderText("Edit Phone");
        Optional<String> phoneResult = phone.showAndWait();

        if(phoneResult.isEmpty()) return;

        TextInputDialog address = new TextInputDialog(s.getAddress());
        address.setHeaderText("Edit Address");
        Optional<String> addressResult = address.showAndWait();

        if(addressResult.isEmpty()) return;

        s.setSupplierName(nameResult.get());
        s.setPhone(phoneResult.get());
        s.setAddress(addressResult.get());

        supplierDAO.update(s);

        loadSuppliers();
    }

    // DELETE
    @FXML
    void handleDeleteSupplier(ActionEvent e){

        Supplier s = supplierTable.getSelectionModel().getSelectedItem();

        if(s == null){
            showAlert("Please select supplier");
            return;
        }

        supplierDAO.delete(s);

        loadSuppliers();
    }

    // SORT
    @FXML
    void handleSort(ActionEvent e){

        if(sortAsc){

            FXCollections.sort(
                    supplierList,
                    Comparator.comparing(Supplier::getSupplierName)
            );

        }else{

            FXCollections.sort(
                    supplierList,
                    Comparator.comparing(Supplier::getSupplierName).reversed()
            );

        }

        sortAsc = !sortAsc;
    }

    // SEARCH
    @FXML
    void handleSearch(ActionEvent e){

        String keyword = searchField.getText().toLowerCase();

        supplierTable.setItems(
                supplierList.filtered(
                        s -> s.getSupplierName().toLowerCase().contains(keyword)
                )
        );
    }

    // FILTER
    @FXML
    void handleFilter(ActionEvent e){

        String option = filterBox.getValue();

        if(option.equals("All")){

            supplierTable.setItems(supplierList);

        }else if(option.equals("A-M")){

            supplierTable.setItems(
                    supplierList.filtered(
                            s -> s.getSupplierName().toUpperCase().charAt(0) <= 'M'
                    )
            );

        }else{

            supplierTable.setItems(
                    supplierList.filtered(
                            s -> s.getSupplierName().toUpperCase().charAt(0) > 'M'
                    )
            );
        }
    }

    private void showAlert(String text){

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(text);
        a.showAndWait();
    }
}
