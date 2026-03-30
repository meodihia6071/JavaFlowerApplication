package flowershop.controllers;

import flowershop.dao.ProductDAO;
import flowershop.models.Customer;
import flowershop.models.Product;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.event.ActionEvent;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FlowersController {

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cboPriceRange;

    @FXML
    private CheckBox chkFresh;

    @FXML
    private CheckBox chkBirthday;

    @FXML
    private CheckBox chkWedding;

    @FXML
    private CheckBox chkFormal;

    @FXML
    private CheckBox chkInStockOnly;

    @FXML
    private Label lblPage;

    @FXML
    private Button btnNext;

    @FXML
    private Button btnPrev;

    @FXML
    private ComboBox<String> cboSortBy;

    @FXML
    private TilePane productTilePane;

    @FXML
    private Button btnCart;

    @FXML
    public void goProfile() {
        SceneManager.switchScene("/fxml/Profile.fxml", "Profile");
    }

    private final ProductDAO productDAO = new ProductDAO();
    private final CartService cartService = new CartService();
    private final DecimalFormat moneyFormat = new DecimalFormat("0.##");
    private int currentPage = 1;
    private int itemsPerPage = 10;
    private List<Product> currentList = new ArrayList<>();

    private List<Product> randomizedProducts = new ArrayList<>();

    @FXML
    public void initialize() {
        setupFilterControls();
        loadInitialProducts();
        refreshCartButtonText();

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void refreshCartButtonText() {
        if (btnCart == null) return;

        Customer customer = SessionManager.getCurrentCustomer();
        int cartCount = cartService.getCartQuantity(customer);

        btnCart.setText("Cart (" + cartCount + ")");
    }

//    PAGINATION
    private void updatePageLabel() {
        if (lblPage == null) return;

        int totalPages = (currentList == null || currentList.isEmpty())
                ? 1
                : (int) Math.ceil((double) currentList.size() / itemsPerPage);

        lblPage.setText("Page " + currentPage + " / " + totalPages);

        if (btnNext != null)
            btnNext.setDisable(currentPage >= totalPages || currentList.isEmpty());

        if (btnPrev != null)
            btnPrev.setDisable(currentPage <= 1 || currentList.isEmpty());
    }
    @FXML
    private void handleNextPage() {
        if (currentList == null || currentList.isEmpty()) return;

        int totalPages = (int) Math.ceil((double) currentList.size() / itemsPerPage);

        if (currentPage < totalPages) {
            currentPage++;
            displayProducts(currentList);
            updatePageLabel();
        }
    }

    @FXML
    private void handlePrevPage() {
        if (currentList == null || currentList.isEmpty()) return;

        if (currentPage > 1) {
            currentPage--;
            displayProducts(currentList);
            updatePageLabel();
        }
    }

    private void setupFilterControls() {
        cboPriceRange.getItems().addAll(
                "Tất cả giá",
                "Dưới 25,000VND",
                "25,000 - 100,000VND",
                "100,000 - 450,000VND",
                "Lớn hơn 450,000VND"
        );
        cboPriceRange.setValue("Tất cả giá");

        cboSortBy.getItems().addAll(
                "Default",
                "Name A-Z",
                "Price low to high",
                "Price high to low"
        );
        cboSortBy.setValue("Default");
    }

    private void loadInitialProducts() {
        List<Product> allProducts = productDAO.findAllWithCategory();
        randomizedProducts = new ArrayList<>(allProducts);
        Collections.shuffle(randomizedProducts);
        displayProducts(randomizedProducts);
    }

    @FXML
    public void handleShowResults() {
        applyFilters();
    }

    private void applyFilters() {
        List<Product> filtered = new ArrayList<>(randomizedProducts);
        currentPage = 1;

        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        if (!keyword.isBlank()) {
            filtered.removeIf(product ->
                    product.getProductName() == null ||
                            !product.getProductName().toLowerCase(Locale.ROOT).contains(keyword)
            );
        }

        List<String> selectedCategories = getSelectedCategories();
        if (!selectedCategories.isEmpty()) {
            filtered.removeIf(product -> {
                String categoryName = product.getCategory() != null ? product.getCategory().getCategoryName() : "";
                return !selectedCategories.contains(categoryName);
            });
        }

        String priceRange = cboPriceRange.getValue();
        if (priceRange != null && !priceRange.equals("Tất cả giá")) {
            filtered.removeIf(product -> !matchPriceRange(product.getPrice(), priceRange));
        }

        if (chkInStockOnly.isSelected()) {
            filtered.removeIf(product -> product.getQuantity() <= 0);
        }

        String sortBy = cboSortBy.getValue();
        if (sortBy != null) {
            switch (sortBy) {
                case "Name A-Z" -> filtered.sort(Comparator.comparing(
                        product -> product.getProductName().toLowerCase(Locale.ROOT)
                ));
                case "Price low to high" -> filtered.sort(Comparator.comparing(Product::getPrice));
                case "Price high to low" -> filtered.sort(Comparator.comparing(Product::getPrice).reversed());
                default -> {
                }
            }
        }

        displayProducts(filtered);
    }

    private List<String> getSelectedCategories() {
        List<String> selected = new ArrayList<>();

        if (chkFresh.isSelected()) {
            selected.add("Hoa Tươi");
        }
        if (chkBirthday.isSelected()) {
            selected.add("Hoa Sinh Nhật");
        }
        if (chkWedding.isSelected()) {
            selected.add("Hoa Cưới");
        }
        if (chkFormal.isSelected()) {
            selected.add("Hoa Trang Trọng");
        }

        return selected;
    }

    private boolean matchPriceRange(BigDecimal price, String priceRange) {
        if (price == null) return false;

        return switch (priceRange) {
            case "Dưới 25,000VND" -> price.compareTo(new BigDecimal("25000")) < 0;
            case "25,000 - 100,000VND" ->
                    price.compareTo(new BigDecimal("25000")) >= 0 &&
                            price.compareTo(new BigDecimal("100000")) <= 0;
            case "100,000 - 450,000VND" ->
                    price.compareTo(new BigDecimal("100000")) >= 0 &&
                            price.compareTo(new BigDecimal("450000")) <= 0;
            case "Lớn hơn 450,000VND" -> price.compareTo(new BigDecimal("450000")) > 0;
            default -> true;
        };
    }

    private void displayProducts(List<Product> products) {
        int totalPages = (int) Math.ceil((double) products.size() / itemsPerPage);

        if (currentPage > totalPages) {
            currentPage = totalPages == 0 ? 1 : totalPages;
        }
        if (products == null || products.isEmpty()) {
            productTilePane.getChildren().clear();

            Label emptyLabel = new Label("No flowers found.");
            emptyLabel.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 20px; -fx-font-weight: 700;");
            productTilePane.getChildren().add(emptyLabel);

            currentList = new ArrayList<>();
            currentPage = 1;
            updatePageLabel();
            return;
        }
        productTilePane.getChildren().clear();
        currentList = products;

        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, products.size());

        for (int i = start; i < end; i++) {
            productTilePane.getChildren().add(createProductCard(products.get(i)));
        }
        updatePageLabel();
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(5);
        card.setPrefWidth(200);
        card.setPrefHeight(280);
        card.setStyle("-fx-background-color: #f5dce2; -fx-padding: 10;");

        ImageView imageView = new ImageView(loadProductImage(product.getImage()));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);

        Label nameLabel = new Label(product.getProductName());
        nameLabel.setStyle("-fx-text-fill: #9b6666; -fx-font-size: 16px;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(150);
        nameLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label(formatMoney(product.getPrice()) + " VND");
        priceLabel.setStyle("-fx-text-fill: #a56767; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label stockLabel = new Label("Stock: " + product.getQuantity());
        stockLabel.setStyle("-fx-text-fill: #b07a7a; -fx-font-size: 13px;");

        Button addButton = new Button("Add to Cart");
        addButton.setUserData(product.getProductName());
        addButton.setOnAction(this::handleAddToCart);
        addButton.getStyleClass().add("primary-button");
        if (product.getQuantity() <= 0) {
            addButton.setText("Out of stock");
            addButton.setDisable(true);
        }
        applyHoverEffect(card, nameLabel);

        card.setOnMouseClicked(event -> {
            if (event.getTarget() instanceof Button) {
                return;
            }

            ProductDetailDialogController.showDialog(product, card.getScene().getWindow());
            refreshCartButtonText();
        });

        card.getChildren().addAll(imageView, nameLabel, priceLabel, stockLabel, addButton);
        return card;
    }

    private void applyHoverEffect(VBox card, Label nameLabel) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), card);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        String normalStyle = nameLabel.getStyle() == null ? "" : nameLabel.getStyle();
        String hoverStyle = normalStyle + "; -fx-text-fill: #cf4f84;";

        card.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
            nameLabel.setStyle(hoverStyle);
        });

        card.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
            nameLabel.setStyle(normalStyle);
        });
    }

    private Image loadProductImage(String imageName) {
        try {
            InputStream stream = null;

            if (imageName != null && !imageName.isBlank()) {
                stream = getClass().getResourceAsStream("/images/" + imageName);
            }

            if (stream == null) {
                stream = getClass().getResourceAsStream("/images/flower-rose.jpg");
            }

            return new Image(stream);
        } catch (Exception e) {
            InputStream fallback = getClass().getResourceAsStream("/images/flower-rose.jpg");
            return new Image(fallback);
        }
    }

    private String formatMoney(BigDecimal amount) {
        return amount == null ? "0" : moneyFormat.format(amount);
    }

    @FXML
    public void handleAddToCart(ActionEvent event) {
        Customer customer = SessionManager.getCurrentCustomer();

        try {
            Button button = (Button) event.getSource();
            String productName = button.getUserData() != null ? button.getUserData().toString().trim() : "";

            if (productName.isBlank()) {
                throw new IllegalArgumentException("Không xác định được sản phẩm để thêm vào giỏ.");
            }
            Product product = productDAO.findByProductName(productName);

            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại.");
            }
            int stock = product.getQuantity();

            int currentInCart = cartService.getQuantityByProduct(customer, productName);

            if (currentInCart + 1 > stock) {
                showInfo("Thông báo", "Đã đạt giới hạn tồn kho!");
                return;
            }

            cartService.addToCart(customer, productName);
            product.setQuantity(stock - 1);
            applyFilters();
            refreshCartButtonText();
            event.consume();
        } catch (Exception e) {
            showInfo("Lỗi", e.getMessage());
        }
    }

    @FXML
    public void goHome() {
        SceneManager.switchScene("/fxml/UserHome.fxml", "User Home");
    }

    @FXML
    public void goFlowers() {
        SceneManager.switchScene("/fxml/Flowers.fxml", "Flowers");
    }

    @FXML
    public void goCategories() {
        SceneManager.switchScene("/fxml/Categories.fxml", "Categories");
    }

    @FXML
    public void goContact() {
        SceneManager.switchScene("/fxml/Contact.fxml", "Contact");
    }

    @FXML
    public void goCart() {
        SceneManager.switchScene("/fxml/Cart.fxml", "Cart");
    }

    @FXML
    public void handleLogout() {
        AuthController.logout();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}