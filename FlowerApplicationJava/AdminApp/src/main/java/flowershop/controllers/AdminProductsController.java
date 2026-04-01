package flowershop.controllers;

import flowershop.dao.ProductDAO;
import flowershop.models.Product;
import flowershop.models.Category;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.utils.HibernateUtil;

import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
                new SimpleStringProperty(cellData.getValue().getCategory() != null ? cellData.getValue().getCategory().getCategoryName() : "N/A")
        );

        colPrice.setCellFactory(column -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if(empty || price == null){
                    setText(null);
                } else {
                    setText(price.setScale(2, RoundingMode.HALF_UP).toString() + "VND");
                }
            }
        });

        // BẢNG HIỂN THỊ ẢNH ĐẸP, VỪA VẶN
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colImage.setCellFactory(column -> new TableCell<Product, String>() {
            private final ImageView imageView = new ImageView();
            private final StackPane container = new StackPane(imageView);

            {
                productTable.setFixedCellSize(70);
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(60);

                container.setAlignment(Pos.CENTER);
                container.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    File file = new File(System.getProperty("user.dir"), imagePath);
                    if (file.exists()) {
                        try {
                            imageView.setImage(new Image(file.toURI().toString()));
                            setGraphic(container);
                            setAlignment(Pos.CENTER);
                        } catch (Exception e) {
                            System.out.println("Lỗi load ảnh: " + e.getMessage());
                            setGraphic(null);
                        }
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        loadProducts();
        setActiveMenu("Products");

        // ========================================================
        // ======= ĐÃ FIX: SỰ KIỆN DOUBLE CLICK ĐỂ XEM CHI TIẾT ===
        // ========================================================
        productTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Product selected = row.getItem();
                    handleViewProductDetails(selected);
                    javafx.application.Platform.runLater(() -> productTable.getSelectionModel().clearSelection());
                }
            });
            return row;
        });
    }

    // ========================================================
    // ======= HÀM VẼ CỬA SỔ XEM CHI TIẾT XỊN XÒ =======
    // ========================================================
    private void handleViewProductDetails(Product selected) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Product Details - " + selected.getProductName());
        applySafeCss(dialog.getDialogPane());

        ImageView bigImageView = new ImageView();
        bigImageView.setFitWidth(250); bigImageView.setFitHeight(250);
        bigImageView.setPreserveRatio(true);
        bigImageView.setStyle("-fx-border-color: #E25A84; -fx-border-width: 2px; -fx-border-radius: 5px;");

        if (selected.getImage() != null && !selected.getImage().isEmpty()) {
            File file = new File(System.getProperty("user.dir"), selected.getImage());
            if (file.exists()) {
                bigImageView.setImage(new Image(file.toURI().toString()));
            }
        }
        VBox imageBox = new VBox(bigImageView);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10);
        grid.setPadding(new Insets(10));

        addDetailRow(grid, 0, "ID Sản phẩm:", String.valueOf(selected.getProductId()));
        addDetailRow(grid, 1, "Tên hoa:", selected.getProductName());
        addDetailRow(grid, 2, "Danh mục:", selected.getCategory() != null ? selected.getCategory().getCategoryName() : "N/A");
        addDetailRow(grid, 3, "Giá bán:", selected.getPrice().setScale(2, RoundingMode.HALF_UP).toString() + "VND");
        addDetailRow(grid, 4, "Tồn kho:", String.valueOf(selected.getQuantity()) + " cành");

        HBox mainLayout = new HBox(20, imageBox, grid);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(mainLayout);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void addDetailRow(GridPane grid, int row, String labelText, String valueText) {
        Label lblLabel = new Label(labelText);
        lblLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a2c2c; -fx-font-size: 14px;");

        Label lblValue = new Label(valueText);
        lblValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #e25a84;");

        grid.add(lblLabel, 0, row);
        grid.add(lblValue, 1, row);
    }

    // ================= LOAD =================
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

    private void applySafeCss(DialogPane dialogPane) {
        URL cssUrl = getClass().getResource("/css/admin-style.css");
        if (cssUrl != null) dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }

    // ================= XỬ LÝ COPY FILE ẢNH =================
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

    // ================= ADD =================
    @FXML
    public void handleAddProduct(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        applySafeCss(dialog.getDialogPane());

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField qtyField = new TextField();

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100); imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-color: #E25A84; -fx-border-width: 1px;");

        final File[] selectedFile = new File[1];
        Button btnChoose = new Button("Chọn ảnh");
        btnChoose.setStyle("-fx-background-color: #f4c7d3; -fx-text-fill: #4a2c2c; -fx-font-weight: bold;");
        btnChoose.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(productTable.getScene().getWindow());
            if(file != null) {
                selectedFile[0] = file;
                imageView.setImage(new Image(file.toURI().toString()));
            }
        });
        VBox imageBox = new VBox(5, imageView, btnChoose);
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
        grid.setHgap(15); grid.setVgap(10);
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

                if (selectedFile[0] != null) {
                    String savedPath = saveImageToProject(selectedFile[0]);
                    if (savedPath != null) p.setImage(savedPath);
                }

                productDAO.save(p);
                loadProducts();
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Dữ liệu nhập không hợp lệ!");
                applySafeCss(a.getDialogPane()); a.show();
            }
        }
    }

    // ================= EDIT =================
    @FXML
    public void handleEditProduct(){
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert a = new Alert(Alert.AlertType.WARNING,"Vui lòng chọn sản phẩm cần sửa!");
            applySafeCss(a.getDialogPane()); a.show();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        applySafeCss(dialog.getDialogPane());

        TextField nameField = new TextField(selected.getProductName());
        TextField priceField = new TextField(selected.getPrice().toString());
        TextField qtyField = new TextField(String.valueOf(selected.getQuantity()));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100); imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        if (selected.getImage() != null && !selected.getImage().isEmpty()) {
            File oldFile = new File(System.getProperty("user.dir"), selected.getImage());
            if (oldFile.exists()) imageView.setImage(new Image(oldFile.toURI().toString()));
        }

        final File[] selectedFile = new File[1];
        Button btnChoose = new Button("Đổi ảnh mới");
        btnChoose.setStyle("-fx-background-color: #f4c7d3; -fx-text-fill: #4a2c2c; -fx-font-weight: bold;");
        btnChoose.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(productTable.getScene().getWindow());
            if(file != null) {
                selectedFile[0] = file;
                imageView.setImage(new Image(file.toURI().toString()));
            }
        });
        VBox imageBox = new VBox(5, imageView, btnChoose);
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
        grid.setHgap(10); grid.setVgap(10);
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

    // ================= DELETE =================
    @FXML
    public void handleDeleteProduct(){
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            Alert a = new Alert(Alert.AlertType.WARNING,"Vui lòng chọn sản phẩm để xóa!");
            applySafeCss(a.getDialogPane()); a.show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        applySafeCss(confirm.getDialogPane());
        confirm.setContentText("Bạn có chắc chắn muốn xóa " + selected.getProductName() + "?");

        if(confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK){
            productDAO.delete(selected);
            loadProducts();
        }
    }

    // ================= SEARCH & SORT =================
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

    @FXML public void sortNameAZ(){ FXCollections.sort(productList, (a,b)->a.getProductName().compareToIgnoreCase(b.getProductName())); }
    @FXML public void sortNameZA(){ FXCollections.sort(productList, (a,b)->b.getProductName().compareToIgnoreCase(a.getProductName())); productTable.setItems(productList); }
    @FXML public void sortPriceAsc(){ FXCollections.sort(productList, (a,b)->a.getPrice().compareTo(b.getPrice())); }

    @FXML
    private void sortPriceDesc() {
        FXCollections.sort(productList, (a,b)->b.getPrice().compareTo(a.getPrice()));
    }

    @FXML
    private void sortNewest() {
        FXCollections.sort(productList, (a,b)->Integer.compare(b.getProductId(), a.getProductId()));
    }

    @FXML public void sortQuantityDesc(){ FXCollections.sort(productList, (a,b)->Integer.compare(b.getQuantity(), a.getQuantity())); }

    // ================= MENU & EFFECTS =================
    private void addSmoothHoverEffect(Button btn) {
        ScaleTransition in = new ScaleTransition(Duration.seconds(0.3), btn);
        in.setToX(1.05); in.setToY(1.05);
        ScaleTransition out = new ScaleTransition(Duration.seconds(0.3), btn);
        out.setToX(1.0); out.setToY(1.0);
        btn.setOnMouseEntered(e -> { out.stop(); in.playFromStart(); });
        btn.setOnMouseExited(e -> { in.stop(); out.playFromStart(); });
    }

    private void setActiveMenu(String name){
        if(sidebar == null) return;
        for(Node node : sidebar.getChildren()){
            if(node instanceof Button btn){
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
    @FXML public void handleLogout(){ SessionManager.clear(); SceneManager.switchScene("/fxml/login.fxml","Login"); }
}