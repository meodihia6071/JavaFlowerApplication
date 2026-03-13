package flowershop.controllers;

import flowershop.services.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.TilePane;

public class UserHomeController {

    @FXML
    private TilePane categoryPane;

    @FXML
    private TilePane productPane;

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
    public void handleAddToCart() {
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