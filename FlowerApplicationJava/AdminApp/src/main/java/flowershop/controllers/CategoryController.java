package flowershop.controllers;

import flowershop.models.Customer;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

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

    private final CartService cartService = new CartService();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            wireAddToCartButtons();

            Platform.runLater(this::scrollToSelectedCategory);
        });
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Contact");
        alert.setContentText("Chưa code màn Contact.");
        alert.showAndWait();
    }

    @FXML
    public void goCart() {
        SceneManager.switchScene("/fxml/Cart.fxml", "Cart");
    }

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
            SceneManager.switchScene("/fxml/Cart.fxml", "Cart");
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