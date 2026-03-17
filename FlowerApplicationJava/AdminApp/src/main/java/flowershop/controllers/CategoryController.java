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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class CategoryController {

    @FXML
    private ScrollPane categoryScroll;

    @FXML
    private VBox birthdaySection;

    @FXML
    private VBox weddingSection;

    @FXML
    private VBox anniversarySection;

    @FXML
    private VBox sympathySection;

    @FXML
    private Button btnCart;

    private final CartService cartService = new CartService();
    private final ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            wireAddToCartButtons();
            wireProductCardClicks();
            applyHoverEffectsToContent();
            scrollToSelectedCategory();
            refreshCartButtonText();
        });
    }

    private void refreshCartButtonText() {
        if (btnCart == null) return;

        Customer customer = SessionManager.getCurrentCustomer();
        int cartCount = cartService.getCartQuantity(customer);

        btnCart.setText(cartCount > 0 ? "Cart (" + cartCount + ")" : "Cart");
    }

    private void wireAddToCartButtons() {
        if (categoryScroll == null || categoryScroll.getContent() == null) return;

        List<Button> buttons = new ArrayList<>();
        collectButtons(categoryScroll.getContent(), buttons);

        for (Button button : buttons) {
            if ("Add to Cart".equalsIgnoreCase(button.getText())) {
                button.setOnAction(this::handleAddToCart);
            }
        }
    }

    private void wireProductCardClicks() {
        if (categoryScroll == null || categoryScroll.getContent() == null) return;

        List<VBox> cards = new ArrayList<>();
        collectCardVBoxes(categoryScroll.getContent(), cards);

        for (VBox card : cards) {
            card.setOnMouseClicked(event -> {
                if (event.getTarget() instanceof Button) {
                    return;
                }

                String productName = extractProductNameFromCard(card);
                if (productName == null || productName.isBlank()) return;

                openProductDetail(productName, card);
            });
        }
    }

    private String extractProductNameFromCard(VBox card) {
        for (Node child : card.getChildren()) {
            if (child instanceof javafx.scene.control.Label label) {
                String text = label.getText();
                if (text != null && !text.isBlank() && !text.startsWith("$")) {
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

    private void collectButtons(Node node, List<Button> buttons) {
        if (node instanceof Button button) {
            buttons.add(button);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectButtons(child, buttons);
            }
        }
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
        if (categoryScroll == null || categoryScroll.getContent() == null) return;

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
            if (text == null) continue;

            String trimmed = text.trim();
            if (trimmed.isBlank()) continue;
            if (trimmed.startsWith("$")) continue;
            if (trimmed.equalsIgnoreCase("Add to Cart")) continue;

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
            case "Birthday Flowers" -> birthdaySection;
            case "Wedding Flowers" -> weddingSection;
            case "Anniversary Flowers" -> anniversarySection;
            case "Sympathy Flowers" -> sympathySection;
            default -> null;
        };

        if (targetSection == null || categoryScroll == null || categoryScroll.getContent() == null) {
            SessionManager.setSelectedCategory(null);
            return;
        }

        Bounds contentBounds = categoryScroll.getContent().getLayoutBounds();
        Bounds viewportBounds = categoryScroll.getViewportBounds();
        Bounds targetBounds = targetSection.getBoundsInParent();

        double contentHeight = contentBounds.getHeight();
        double viewportHeight = viewportBounds.getHeight();
        double targetY = targetBounds.getMinY();

        double vValue;
        if (contentHeight <= viewportHeight) {
            vValue = 0;
        } else {
            vValue = targetY / (contentHeight - viewportHeight);
        }

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