package flowershop.controllers;

import flowershop.models.Customer;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;
import java.util.List;

public class UserHomeController {

    @FXML
    private TilePane categoryPane;

    @FXML
    private TilePane productPane;

    private final CartService cartService = new CartService();

    @FXML
    public void initialize() {
        if (categoryPane != null) {
            categoryPane.widthProperty().addListener((obs, oldVal, newVal) -> {
                double width = newVal.doubleValue();
                if (width > 1100) {
                    categoryPane.setPrefColumns(5);
                } else if (width > 850) {
                    categoryPane.setPrefColumns(4);
                } else if (width > 650) {
                    categoryPane.setPrefColumns(3);
                } else {
                    categoryPane.setPrefColumns(2);
                }
            });
        }

        if (productPane != null) {
            productPane.widthProperty().addListener((obs, oldVal, newVal) -> {
                double width = newVal.doubleValue();
                if (width > 1200) {
                    productPane.setPrefColumns(5);
                } else if (width > 950) {
                    productPane.setPrefColumns(4);
                } else if (width > 720) {
                    productPane.setPrefColumns(3);
                } else {
                    productPane.setPrefColumns(2);
                }
            });
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
        SessionManager.setSelectedCategory(null);
        SceneManager.switchScene("/fxml/Categories.fxml", "Categories");
    }

    @FXML
    public void handleCategoryClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        String categoryName = button.getText();

        SessionManager.setSelectedCategory(categoryName);
        SceneManager.switchScene("/fxml/Categories.fxml", "Categories");
    }

    @FXML
    public void goContact() {
        showInfo("Contact", "Vẽ nốt Contact đy-.-.");
    }

    @FXML
    public void goCart() {
        SceneManager.switchScene("/fxml/Cart.fxml", "Cart");
    }

    @FXML
    public void handleAddToCart(ActionEvent event) {
        Customer customer = SessionManager.getCurrentCustomer();

        try {
            String productName = resolveProductName((Button) event.getSource());
            cartService.addToCart(customer, productName);
            SceneManager.switchScene("/fxml/Cart.fxml", "Cart");
        } catch (Exception e) {
            showInfo("Lỗi", e.getMessage());
        }
    }

    private String resolveProductName(Button sourceButton) {
        if (sourceButton.getUserData() != null) {
            return sourceButton.getUserData().toString().trim();
        }

        Node parent = sourceButton.getParent();
        if (parent instanceof Parent card) {
            List<Label> labels = new ArrayList<>();
            collectLabels(card, labels);

            for (Label label : labels) {
                String text = label.getText();
                if (text != null && !text.isBlank() && !text.trim().startsWith("$")) {
                    return text.trim();
                }
            }
        }

        throw new IllegalArgumentException("Không đọc được tên sản phẩm từ giao diện.");
    }

    private void collectLabels(Node node, List<Label> labels) {
        if (node instanceof Label label) {
            labels.add(label);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                collectLabels(child, labels);
            }
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