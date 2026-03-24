package flowershop.controllers;

import flowershop.dao.CategoryDAO;
import flowershop.models.Category;
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

public class AdminCategoriesController {

    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> colId;
    @FXML private TableColumn<Category, String> colName;

    @FXML private TextField searchField;
    @FXML private VBox sidebar;

    private CategoryDAO categoryDAO = new CategoryDAO();
    private ObservableList<Category> categoryList;

    // ================= INIT =================

    @FXML
    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadCategories();
        setActiveMenu("Categories");
    }

    // ================= LOAD =================

    private void loadCategories(){
        List<Category> list = categoryDAO.getAllCategories();
        categoryList = FXCollections.observableArrayList(list);
        categoryTable.setItems(categoryList);
    }

    // ================= ADD =================

    @FXML
    public void handleAddCategory(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Category");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField nameField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        grid.add(new Label("Category Name:"),0,0);
        grid.add(nameField,1,0);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            if(nameField.getText().trim().isEmpty()){
                new Alert(Alert.AlertType.WARNING,"Name cannot be empty!").show();
                return;
            }

            Category c = new Category();
            c.setCategoryName(nameField.getText());

            categoryDAO.save(c);
            loadCategories();
        }
    }

    // ================= EDIT =================

    @FXML
    public void handleEditCategory(){

        Category selected = categoryTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            new Alert(Alert.AlertType.WARNING,"Select category first!").show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Category");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField nameField = new TextField(selected.getCategoryName());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        grid.add(new Label("Category Name:"),0,0);
        grid.add(nameField,1,0);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            selected.setCategoryName(nameField.getText());

            categoryDAO.update(selected);
            loadCategories();
        }
    }

    // ================= DELETE =================

    @FXML
    public void handleDeleteCategory(){

        Category selected = categoryTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            new Alert(Alert.AlertType.WARNING,"Select category!").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        confirm.getDialogPane().getStyleClass().add("dialog-pane");
        confirm.setContentText("Delete " + selected.getCategoryName() + "?");

        if(confirm.showAndWait().get() == ButtonType.OK){
            categoryDAO.delete(selected);
            loadCategories();
        }
    }

    // ================= SEARCH =================

    @FXML
    public void handleSearch(){

        String keyword = searchField.getText().toLowerCase();

        if(keyword.isEmpty()){
            categoryTable.setItems(categoryList);
            return;
        }

        ObservableList<Category> filtered = FXCollections.observableArrayList();

        for(Category c : categoryList){
            if(c.getCategoryName().toLowerCase().contains(keyword)){
                filtered.add(c);
            }
        }

        categoryTable.setItems(filtered);
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