package flowershop.controllers;

import flowershop.dao.ProductDAO;
import flowershop.models.Product;
import flowershop.models.Category;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.utils.HibernateUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class AdminProductsController {

    @FXML private TableView<Product> productTable;

    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, BigDecimal> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colCategory;

    @FXML private TextField searchField;
    @FXML private VBox sidebar;

    private ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList;

    // ================= INIT =================

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getCategory().getCategoryName()
                )
        );
        colPrice.setCellFactory(column -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);

                if(empty || price == null){
                    setText(null);
                } else {
                    setText(price.setScale(2, RoundingMode.HALF_UP).toString());
                }
            }
        });

        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadProducts();
        setActiveMenu("Products");
    }

    // ================= LOAD =================

    private void loadProducts(){
        List<Product> list = productDAO.getAllProducts();
        productList = FXCollections.observableArrayList(list);
        productTable.setItems(productList);
    }

    // ================= CATEGORY LIST =================

    private ObservableList<Category> getCategories(){

        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Category> list = session.createQuery("FROM Category", Category.class).list();
        session.close();

        return FXCollections.observableArrayList(list);
    }

    // ================= ADD =================

    @FXML
    public void handleAddProduct(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField qtyField = new TextField();
        TextField imageField = new TextField();

        ComboBox<Category> cbCategory = new ComboBox<>(getCategories());
        cbCategory.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? null : item.getCategoryName());
            }
        });
        cbCategory.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? null : item.getCategoryName());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        grid.add(new Label("Name:"),0,0);
        grid.add(nameField,1,0);

        grid.add(new Label("Price:"),0,1);
        grid.add(priceField,1,1);

        grid.add(new Label("Quantity:"),0,2);
        grid.add(qtyField,1,2);

        grid.add(new Label("Image:"),0,3);
        grid.add(imageField,1,3);

        grid.add(new Label("Category:"),0,4);
        grid.add(cbCategory,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            Product p = new Product();

            p.setProductName(nameField.getText());
            p.setPrice(new BigDecimal(priceField.getText()));
            p.setQuantity(Integer.parseInt(qtyField.getText()));
            p.setImage(imageField.getText());
            p.setCategory(cbCategory.getValue());

            productDAO.save(p);
            loadProducts();
        }
    }

    // ================= EDIT =================

    @FXML
    public void handleEditProduct(){

        Product selected = productTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            new Alert(Alert.AlertType.WARNING,"Select product first!").show();

            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        TextField nameField = new TextField(selected.getProductName());
        TextField priceField = new TextField(selected.getPrice().toString());
        TextField qtyField = new TextField(String.valueOf(selected.getQuantity()));
        TextField imageField = new TextField(selected.getImage());

        ComboBox<Category> cbCategory = new ComboBox<>(getCategories());
        cbCategory.setValue(selected.getCategory());

        cbCategory.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? null : item.getCategoryName());
            }
        });

        cbCategory.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? null : item.getCategoryName());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Name:"),0,0);
        grid.add(nameField,1,0);

        grid.add(new Label("Price:"),0,1);
        grid.add(priceField,1,1);

        grid.add(new Label("Quantity:"),0,2);
        grid.add(qtyField,1,2);

        grid.add(new Label("Image:"),0,3);
        grid.add(imageField,1,3);

        grid.add(new Label("Category:"),0,4);
        grid.add(cbCategory,1,4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){

            selected.setProductName(nameField.getText());
            selected.setPrice(new BigDecimal(priceField.getText()));
            selected.setQuantity(Integer.parseInt(qtyField.getText()));
            selected.setImage(imageField.getText());
            selected.setCategory(cbCategory.getValue());

            productDAO.update(selected);
            loadProducts();
        }
    }

    // ================= DELETE =================

    @FXML
    public void handleDeleteProduct(){

        Product selected = productTable.getSelectionModel().getSelectedItem();

        if(selected == null){
            new Alert(Alert.AlertType.WARNING,"Select product!").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/admin-style.css").toExternalForm()
        );
        confirm.getDialogPane().getStyleClass().add("dialog-pane");
        confirm.setContentText("Delete " + selected.getProductName() + "?");

        if(confirm.showAndWait().get() == ButtonType.OK){
            productDAO.delete(selected);
            loadProducts();
        }
    }

    // ================= SEARCH =================

    @FXML
    public void handleSearch(){

        String keyword = searchField.getText().toLowerCase();

        if(keyword.isEmpty()){
            productTable.setItems(productList);
            return;
        }

        ObservableList<Product> filtered = FXCollections.observableArrayList();

        for(Product p : productList){
            if(p.getProductName().toLowerCase().contains(keyword)){
                filtered.add(p);
            }
        }

        productTable.setItems(filtered);
    }

    // ================= SORT =================

    @FXML
    public void sortNameAZ(){
        FXCollections.sort(productList,
                (a,b)->a.getProductName().compareToIgnoreCase(b.getProductName()));
    }

    @FXML
    public void sortNameZA(){
        FXCollections.sort(productList,
                (a,b)->b.getProductName().compareToIgnoreCase(a.getProductName())
        );
        productTable.setItems(productList);
    }

    @FXML
    public void sortPriceAsc(){
        FXCollections.sort(productList,
                (a,b)->a.getPrice().compareTo(b.getPrice()));
    }

    @FXML
    private void sortPriceDesc() {
        System.out.println("Sort price DESC");
        // TODO: viết logic sort
    }

    @FXML
    private void sortNewest() {
        System.out.println("Sort newest");
    }

    @FXML
    public void sortQuantityDesc(){
        FXCollections.sort(productList,
                (a,b)->Integer.compare(b.getQuantity(), a.getQuantity()));
    }

    // ================= MENU =================

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