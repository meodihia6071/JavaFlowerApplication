package flowershop.controllers;

import flowershop.models.Customer;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ContactController {

    @FXML
    private VBox storeCard;

    @FXML
    private VBox formCard;

    @FXML
    private VBox addressBox;

    @FXML
    private VBox phoneBox;

    @FXML
    private VBox emailBox;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextArea txtMessage;

    @FXML
    private Button btnCart;

    private final CartService cartService = new CartService();

    @FXML
    public void initialize() {
        playEntrance(storeCard, 28, 80);
        playEntrance(formCard, 28, 160);
        playEntrance(addressBox, 24, 260);
        playEntrance(phoneBox, 24, 340);
        playEntrance(emailBox, 24, 420);

        addHoverAnimation(storeCard);
        addHoverAnimation(formCard);
        addHoverAnimation(addressBox);
        addHoverAnimation(phoneBox);
        addHoverAnimation(emailBox);

        refreshCartButtonText();
    }

    private void refreshCartButtonText() {
        if (btnCart == null) return;

        Customer customer = SessionManager.getCurrentCustomer();
        if (customer == null) {
            btnCart.setText("Cart");
            return;
        }

        int count = cartService.getCartQuantity(customer);
        btnCart.setText(count > 0 ? "Cart (" + count + ")" : "Cart");
    }

    private void playEntrance(Node node, double fromY, int delayMillis) {
        node.setOpacity(0);
        node.setTranslateY(fromY);

        FadeTransition fade = new FadeTransition(Duration.millis(450), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(450), node);
        slide.setFromY(fromY);
        slide.setToY(0);

        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.setDelay(Duration.millis(delayMillis));
        parallel.play();
    }

    private void addHoverAnimation(Node node) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(18);
        shadow.setOffsetY(5);
        shadow.setColor(Color.rgb(188, 138, 152, 0.28));

        node.setOnMouseEntered(e -> {
            node.setEffect(shadow);

            ScaleTransition scale = new ScaleTransition(Duration.millis(180), node);
            scale.setToX(1.015);
            scale.setToY(1.015);
            scale.play();
        });

        node.setOnMouseExited(e -> {
            node.setEffect(null);

            ScaleTransition scale = new ScaleTransition(Duration.millis(180), node);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });
    }

    @FXML
    private void handleSendMessage() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String message = txtMessage.getText().trim();

        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            showInfo("Thiếu thông tin", "Vui lòng nhập đầy đủ họ tên, email và nội dung tin nhắn.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showInfo("Email chưa hợp lệ", "Vui lòng nhập đúng định dạng email.");
            return;
        }

        showInfo("Gửi thành công", "Cảm ơn bạn đã liên hệ với Flower Shop. Chúng tôi sẽ phản hồi sớm nhất.");
        clearForm();
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        txtName.clear();
        txtEmail.clear();
        txtMessage.clear();
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
    public void goCart() {
        SceneManager.switchScene("/fxml/Cart.fxml", "Cart");
    }

    @FXML
    public void handleLogout() {
        AuthController.logout();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}