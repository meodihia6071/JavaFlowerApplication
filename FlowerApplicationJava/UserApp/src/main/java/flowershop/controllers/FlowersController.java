package flowershop.controllers;

import flowershop.services.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class FlowersController {

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

    @FXML
    public void handleLogout() {
        AuthController.logout();
    }
}