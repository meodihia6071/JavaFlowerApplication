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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import org.hibernate.Session;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AdminProductsController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, BigDecimal> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colImage;
    @FXML private TableColumn<Product, String> colCategory;

    @FXML private TextField searchField;

    private ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList;

    @FXML
    public void initialize(){

        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        colCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory() != null ? cellData.getValue().getCategory().getCategoryName() : "N/A")
        );

        colPrice.setCellFactory(column -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if(empty || price == null) setText(null);
                else setText("$" + price.setScale(2, RoundingMode.HALF_UP).toString());
            }
        });

        // 1. HIỂN THỊ ẢNH TRONG BẢNG (KÍCH THƯỚC 80x80 TO RÕ)
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colImage.setCellFactory(column -> new TableCell<Product, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(80); // Tăng kích thước
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    File file = new File(System.getProperty("user.dir"), imagePath);
                    if (file.exists()) {
                        imageView.setImage(new Image(file.toURI().toString()));
                        setGraphic(imageView);
                        setAlignment(Pos.CENTER);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // 2. BẮT SỰ KIỆN DOUBLE CLICK VÀO SẢN PHẨM ĐỂ XEM ẢNH ZOOM TO
        productTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                Product selected = productTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showProductDetails(selected); // Gọi hàm hiển thị
                }
            }
        });

        loadProducts();
    }

    // ================= XEM CHI TIẾT SẢN PHẨM (ZOOM ẢNH) =================
    private void showProductDetails(Product p) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết: " + p.getProductName());
        applySafeCss(dialog.getDialogPane());

        ImageView zoomImageView = new ImageView();
        zoomImageView.setFitWidth(300); // Kích thước khổng lồ 300x300
        zoomImageView.setFitHeight(300);
        zoomImageView.setPreserveRatio(true);
        zoomImageView.setStyle("-fx-border-color: #E25A84; -fx-border-width: 2px; -fx-padding: 5px;");

        // Load ảnh thật
        if (p.getImage() != null && !p.getImage().isEmpty()) {
            File imgFile = new File(System.getProperty("user.dir"), p.getImage());
            if (imgFile.exists()) {
                zoomImageView.setImage(new Image(imgFile.toURI().toString()));
            }
        }

        // Bảng thông tin bên cạnh ảnh
        VBox infoBox = new VBox(15);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        Label lblName = new Label("Tên SP: " + p.getProductName());
        lblName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #c44d6e;");
        Label lblPrice = new Label("Giá bán: $" + p.getPrice());
        lblPrice.setStyle("-fx-font-size: 16px;");
        Label lblStock = new Label("Còn lại: " + p.getQuantity() + " sản phẩm");
        lblStock.setStyle("-fx-font-size: 16px;");
        Label lblCate = new Label("Danh mục: " + (p.getCategory() != null ? p.getCategory().getCategoryName() : ""));
        lblCate.setStyle("-fx-font-size: 16px;");

        infoBox.getChildren().addAll(lblName, lblPrice, lblStock, lblCate);

        HBox root = new HBox(30, zoomImageView, infoBox);
        root.setPadding(new javafx.geometry.Insets(20));

        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // ================= COPY FILE ẢNH VÀO PROJECT AN TOÀN =================
    private String saveImageToProject(File selectedFile) {
        if (selectedFile == null) return null;
        try {
            String dir = System.getProperty("user.dir") + "/product_images/";
            File dirFile = new File(dir);
            if(!dirFile.exists()) dirFile.mkdirs();

            String name = selectedFile.getName();
            String ext = name.contains(".") ? name.substring(name.lastIndexOf(".")) : ".png";
            String newFileName = "prod_" + System.currentTimeMillis() + ext;

            File dest = new File(dir, newFileName);
            Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "product_images/" + newFileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadProducts(){
        List<Product> list = productDAO.getAllProducts();
        productList = FXCollections.observableArrayList(list);
        productTable.setItems(productList);
    }

    private ObservableList<Category> getCategories(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Category> list = session.createQuery("FROM Category", Category.class).list();
        session.close();
        return FXCollections.observableArrayList(list);
    }

    private void applySafeCss(DialogPane dp) {
        URL url = getClass().getResource("/css/admin-style.css");
        if (url != null) dp.getStylesheets().add(url.toExternalForm());
        dp.getStyleClass().add("dialog-pane");
    }

    // ================= ADD PRODUCT (KÈM CHỌN ẢNH) =================
    @FXML
    public void handleAddProduct(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        applySafeCss(dialog.getDialogPane());

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField qtyField = new TextField();

        // Khung hiển thị ảnh chọn trước
        ImageView previewImage = new ImageView();
        previewImage.setFitWidth(120); previewImage.setFitHeight(120);
        previewImage.setPreserveRatio(true);
        previewImage.setStyle("-fx-border-color: #E25A84;");

        final File[] selectedFile = new File[1];
        Button btnChoose = new Button("Thêm ảnh từ máy");
        btnChoose.getStyleClass().add("tool-btn"); // Dùng class tool-btn để có hover xịn xò
        btnChoose.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if(file != null) {
                selectedFile[0] = file;
                previewImage.setImage(new Image(file.toURI().toString()));
            }
        });
        VBox imageBox = new VBox(10, previewImage, btnChoose);
        imageBox.setAlignment(Pos.CENTER);

        ComboBox<Category> cbCategory = new ComboBox<>(getCategories());
        cbCategory.setCellFactory(lv -> new ListCell<>(){
            @Override protected void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? null : item.getCategoryName());
            }
        });
        cbCategory.setButtonCell(new ListCell<>(){
            @Override protected void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? null : item.getCategoryName());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.add(new Label("Name:"),0,0); grid.add(nameField,1,0);
        grid.add(new Label("Price:"),0,1); grid.add(priceField,1,1);
        grid.add(new Label("Quantity:"),0,2); grid.add(qtyField,1,2);
        grid.add(new Label("Category:"),0,3); grid.add(cbCategory,1,3);
        grid.add(new Label("Image:"),0,4); grid.add(imageBox,1,4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){
            try {
                Product p = new Product();
                p.setProductName(nameField.getText());
                p.setPrice(new BigDecimal(priceField.getText()));
                p.setQuantity(Integer.parseInt(qtyField.getText()));
                p.setCategory(cbCategory.getValue());

                String savedPath = saveImageToProject(selectedFile[0]);
                if (savedPath != null) p.setImage(savedPath);

                productDAO.save(p);
                loadProducts();
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Dữ liệu không hợp lệ!");
                applySafeCss(a.getDialogPane()); a.show();
            }
        }
    }

    // ================= EDIT PRODUCT =================
    @FXML
    public void handleEditProduct(){
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert a = new Alert(Alert.AlertType.WARNING,"Chọn sản phẩm cần sửa!");
            applySafeCss(a.getDialogPane()); a.show(); return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        applySafeCss(dialog.getDialogPane());

        TextField nameField = new TextField(selected.getProductName());
        TextField priceField = new TextField(selected.getPrice().toString());
        TextField qtyField = new TextField(String.valueOf(selected.getQuantity()));

        ImageView previewImage = new ImageView();
        previewImage.setFitWidth(120); previewImage.setFitHeight(120);
        previewImage.setPreserveRatio(true);
        if (selected.getImage() != null && !selected.getImage().isEmpty()) {
            File oldFile = new File(System.getProperty("user.dir"), selected.getImage());
            if (oldFile.exists()) previewImage.setImage(new Image(oldFile.toURI().toString()));
        }

        final File[] selectedFile = new File[1];
        Button btnChoose = new Button("Đổi ảnh khác");
        btnChoose.getStyleClass().add("tool-btn"); // Dùng class tool-btn để có hover xịn xò
        btnChoose.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if(file != null) {
                selectedFile[0] = file;
                previewImage.setImage(new Image(file.toURI().toString()));
            }
        });
        VBox imageBox = new VBox(10, previewImage, btnChoose);
        imageBox.setAlignment(Pos.CENTER);

        ComboBox<Category> cbCategory = new ComboBox<>(getCategories());
        if(selected.getCategory() != null) {
            cbCategory.getItems().stream().filter(c -> c.getCategoryId() == selected.getCategory().getCategoryId()).findFirst().ifPresent(cbCategory::setValue);
        }
        cbCategory.setCellFactory(lv -> new ListCell<>(){
            @Override protected void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? null : item.getCategoryName());
            }
        });
        cbCategory.setButtonCell(new ListCell<>(){
            @Override protected void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                setText(empty ? null : item.getCategoryName());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        grid.add(new Label("Name:"),0,0); grid.add(nameField,1,0);
        grid.add(new Label("Price:"),0,1); grid.add(priceField,1,1);
        grid.add(new Label("Quantity:"),0,2); grid.add(qtyField,1,2);
        grid.add(new Label("Category:"),0,3); grid.add(cbCategory,1,3);
        grid.add(new Label("Image:"),0,4); grid.add(imageBox,1,4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        if(dialog.showAndWait().orElse(ButtonType.CANCEL) == saveBtn){
            try {
                selected.setProductName(nameField.getText());
                selected.setPrice(new BigDecimal(priceField.getText()));
                selected.setQuantity(Integer.parseInt(qtyField.getText()));
                selected.setCategory(cbCategory.getValue());

                if (selectedFile[0] != null) {
                    String savedPath = saveImageToProject(selectedFile[0]);
                    if (savedPath != null) selected.setImage(savedPath);
                }
                productDAO.update(selected);
                loadProducts();
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Lỗi khi cập nhật!");
                applySafeCss(a.getDialogPane()); a.show();
            }
        }
    }

    @FXML
    public void handleDeleteProduct(){
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if(selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        applySafeCss(confirm.getDialogPane());
        confirm.setContentText("Bạn có chắc chắn muốn xóa " + selected.getProductName() + "?");
        if(confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK){
            productDAO.delete(selected);
            loadProducts();
        }
    }

    @FXML public void handleSearch(){
        String keyword = searchField.getText().toLowerCase();
        if(keyword.isEmpty()){ productTable.setItems(productList); return; }
        ObservableList<Product> filtered = FXCollections.observableArrayList();
        for(Product p : productList){
            if(p.getProductName().toLowerCase().contains(keyword)) filtered.add(p);
        }
        productTable.setItems(filtered);
    }

    @FXML public void sortNameAZ(){ FXCollections.sort(productList, (a,b)->a.getProductName().compareToIgnoreCase(b.getProductName())); }
    @FXML public void sortNameZA(){ FXCollections.sort(productList, (a,b)->b.getProductName().compareToIgnoreCase(a.getProductName())); }
    @FXML public void sortPriceAsc(){ FXCollections.sort(productList, (a,b)->a.getPrice().compareTo(b.getPrice())); }
    @FXML private void sortPriceDesc() { FXCollections.sort(productList, (a,b)->b.getPrice().compareTo(a.getPrice())); }
    @FXML private void sortNewest() { FXCollections.sort(productList, (a,b)->Integer.compare(b.getProductId(), a.getProductId())); }

    @FXML public void goDashboard(){ SceneManager.switchScene("/fxml/AdminDashboard.fxml","Dashboard"); }
    @FXML public void goProducts(){ SceneManager.switchScene("/fxml/AdminProducts.fxml","Products"); }
    @FXML public void goCategories(){ SceneManager.switchScene("/fxml/AdminCategories.fxml","Categories"); }
    @FXML public void goOrders(){ SceneManager.switchScene("/fxml/AdminOrders.fxml","Orders"); }
    @FXML public void goCustomers(){ SceneManager.switchScene("/fxml/AdminCustomers.fxml","Customers"); }
    @FXML public void goSuppliers(){ SceneManager.switchScene("/fxml/AdminSuppliers.fxml","Suppliers"); }
    @FXML public void goStock(){ SceneManager.switchScene("/fxml/AdminStock.fxml","Stock"); }
    @FXML public void goEmployees(){ SceneManager.switchScene("/fxml/AdminEmployees.fxml","Employees");}
    @FXML public void goReports(){ SceneManager.switchScene("/fxml/AdminReports.fxml","Reports"); }
    @FXML public void handleLogout(){ SessionManager.clear(); SceneManager.switchScene("/fxml/login.fxml","Login"); }
}