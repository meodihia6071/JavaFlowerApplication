package flowershop.controllers;

import flowershop.dao.ProductDAO;
import flowershop.models.Customer;
import flowershop.models.Product;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class CategoryController {

    @FXML
    private ScrollPane categoryScroll;

    @FXML
    private VBox freshSection;

    @FXML
    private VBox birthdaySection;

    @FXML
    private VBox weddingSection;

    @FXML
    private VBox formalSection;

    @FXML
    private Button btnCart;

    @FXML
    private HBox freshContainer;

    @FXML
    private HBox birthdayContainer;

    @FXML
    private HBox weddingContainer;

    @FXML
    private HBox formalContainer;

    private final CartService cartService = new CartService();
    private final ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            loadAllCategories();
            refreshCartButtonText();

            categoryScroll.applyCss();
            categoryScroll.layout();

            if (categoryScroll.getContent() instanceof Parent parent) {
                parent.applyCss();
                parent.layout();
            }

            Platform.runLater(() -> {
                scrollToSelectedCategory();
                applyHoverEffectsToContent();
            });
        });
    }

    private Image loadImage(String imageName) {
        try {
            if (imageName != null && !imageName.isBlank()) {
                var stream = getClass().getResourceAsStream("/images/" + imageName);
                if (stream != null) {
                    return new Image(stream);
                }
            }
        } catch (Exception ignored) {
        }

        return new Image(getClass().getResourceAsStream("/images/flower-rose.jpg"));
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(6);
        card.setPrefSize(185, 280);
        card.setStyle("-fx-background-color: #f5dce2; -fx-padding: 8;");

        ImageView img = new ImageView(loadImage(product.getImage()));
        img.setFitWidth(150);
        img.setFitHeight(150);

        Label name = new Label(product.getProductName());
        name.setStyle("-fx-font-size: 16px; -fx-text-fill: #9b6666;");

        Label price = new Label(product.getPrice() + " VND");
        price.setStyle("-fx-font-size: 16px; -fx-text-fill: #9b6666;");

        Button btn = new Button("Add to Cart");
        btn.setUserData(product.getProductName());
        btn.getStyleClass().add("primary-button");

        if (product.getQuantity() <= 0) {
            btn.setText("Out of stock");
            btn.setDisable(true);
        }

        btn.setOnAction(this::handleAddToCart);

        card.getChildren().addAll(img, name, price, btn);

        card.setOnMouseClicked(e -> {
            if (!(e.getTarget() instanceof Button)) {
                openProductDetail(product.getProductName(), card);
            }
        });

        return card;
    }

    private void refreshCartButtonText() {
        if (btnCart == null) {
            return;
        }

        Customer customer = SessionManager.getCurrentCustomer();
        int cartCount = cartService.getCartQuantity(customer);

        btnCart.setText("Cart (" + cartCount + ")");
    }

    private void loadCategory(int categoryId, HBox container) {
        List<Product> products = productDAO.findTop5ByCategoryId(categoryId);

        container.getChildren().clear();

        for (Product p : products) {
            container.getChildren().add(createProductCard(p));
        }
    }

    private void loadAllCategories() {
        loadCategory(1, freshContainer);
        loadCategory(2, birthdayContainer);
        loadCategory(3, weddingContainer);
        loadCategory(4, formalContainer);
    }

    private String extractProductNameFromCard(VBox card) {
        for (Node child : card.getChildren()) {
            if (child instanceof Label label) {
                String text = label.getText();
                if (text != null && !text.isBlank() && !text.startsWith("$") && !text.endsWith("VND")) {
                    return text.trim();
                }
            }
        }
        return null;
    }

    private void openProductDetail(String productName, Node ownerNode) {
        Product product = productDAO.findByProductName(productName);
        if (product == null) {
            showInfo("Lỗi", "Không tìm thấy sản phẩm.");
            return;
        }

        ProductDetailDialogController.showDialog(product, ownerNode.getScene().getWindow());
        refreshCartButtonText();
    }

    private void collectCardVBoxes(Node node, List<VBox> cards) {
        if (node instanceof VBox vbox && isCardBox(vbox)) {
            cards.add(vbox);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectCardVBoxes(child, cards);
            }
        }
    }

    private boolean isCardBox(VBox vbox) {
        String style = vbox.getStyle();
        return style != null && style.contains("#f5dce2");
    }

    private void applyHoverEffectsToContent() {
        if (categoryScroll == null || categoryScroll.getContent() == null) {
            return;
        }

        List<VBox> cards = new ArrayList<>();
        collectCardVBoxes(categoryScroll.getContent(), cards);

        for (VBox card : cards) {
            Labeled titleNode = findTitleNode(card);
            if (titleNode != null) {
                applyHoverEffect(card, titleNode);
            }
        }
    }

    private Labeled findTitleNode(Parent parent) {
        List<Labeled> labeledNodes = new ArrayList<>();
        collectLabeledNodes(parent, labeledNodes);

        for (Labeled node : labeledNodes) {
            String text = node.getText();
            if (text == null) {
                continue;
            }

            String trimmed = text.trim();
            if (trimmed.isBlank()) {
                continue;
            }
            if (trimmed.endsWith("VND")) {
                continue;
            }
            if (trimmed.equalsIgnoreCase("Add to Cart")) {
                continue;
            }
            if (trimmed.equalsIgnoreCase("Out of stock")) {
                continue;
            }

            return node;
        }

        return null;
    }

    private void collectLabeledNodes(Node node, List<Labeled> labeledNodes) {
        if (node instanceof Labeled labeled) {
            labeledNodes.add(labeled);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectLabeledNodes(child, labeledNodes);
            }
        }
    }

    private void applyHoverEffect(VBox card, Labeled titleNode) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), card);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        String normalStyle = titleNode.getStyle() == null ? "" : titleNode.getStyle();
        String hoverStyle = normalStyle + "; -fx-text-fill: #cf4f84;";

        card.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
            titleNode.setStyle(hoverStyle);
        });

        card.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
            titleNode.setStyle(normalStyle);
        });
    }

    private void scrollToSelectedCategory() {
        String selectedCategory = SessionManager.getSelectedCategory();

        if (selectedCategory == null || selectedCategory.isBlank()) {
            return;
        }

        VBox targetSection = switch (selectedCategory.trim()) {
            case "Hoa Tươi" -> freshSection;
            case "Hoa Sinh Nhật" -> birthdaySection;
            case "Hoa Cưới" -> weddingSection;
            case "Hoa Trang Trọng" -> formalSection;
            default -> null;
        };

        if (targetSection == null || categoryScroll == null || categoryScroll.getContent() == null) {
            SessionManager.setSelectedCategory(null);
            return;
        }

        categoryScroll.applyCss();
        categoryScroll.layout();
        categoryScroll.applyCss();
        categoryScroll.layout();

        if (categoryScroll.getContent() instanceof Parent parent) {
            parent.applyCss();
            parent.layout();
        }

        Bounds contentBounds = categoryScroll.getContent().getLayoutBounds();
        Bounds viewportBounds = categoryScroll.getViewportBounds();
        Bounds targetBounds = targetSection.getBoundsInParent();

        double contentHeight = contentBounds.getHeight();
        double viewportHeight = viewportBounds.getHeight();
        double targetY = targetBounds.getMinY();

        double maxScroll = contentHeight - viewportHeight;
        double vValue = maxScroll <= 0 ? 0 : targetY / maxScroll;

        categoryScroll.setVvalue(Math.max(0, Math.min(1, vValue)));

        SessionManager.setSelectedCategory(null);
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
        SessionManager.setSelectedCategory(null);
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
    public void handleAddToCart(ActionEvent event) {
        Customer customer = SessionManager.getCurrentCustomer();

        try {
            Button button = (Button) event.getSource();
            String productName = button.getUserData() != null
                    ? button.getUserData().toString().trim()
                    : "";

            if (productName.isBlank()) {
                throw new IllegalArgumentException("Không xác định được sản phẩm để thêm vào giỏ.");
            }

            cartService.addToCart(customer, productName);
            refreshCartButtonText();
            event.consume();
        } catch (Exception e) {
            showInfo("Lỗi", e.getMessage());
        }
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