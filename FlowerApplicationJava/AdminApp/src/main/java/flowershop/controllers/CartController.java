package flowershop.controllers;

import flowershop.models.Customer;
import flowershop.models.OrderDetail;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class CartController {

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblShipping;

    @FXML
    private Label lblTotal;

    @FXML
    private Button btnCart;

    @FXML
    public void goProfile() {
        SceneManager.switchScene("/fxml/Profile.fxml", "Profile");
    }


    private final CartService cartService = new CartService();
    private final DecimalFormat moneyFormat = new DecimalFormat("0.##");

    @FXML
    public void initialize() {
        loadCart();
    }

    private void refreshCartButtonText() {
        if (btnCart == null) return;

        Customer customer = SessionManager.getCurrentCustomer();
        int cartCount = cartService.getCartQuantity(customer);

        btnCart.setText("Cart (" + cartCount + ")");
    }

    private void loadCart() {
        cartItemsContainer.getChildren().clear();

        Customer customer = SessionManager.getCurrentCustomer();
        if (customer == null) {
            cartItemsContainer.getChildren().add(createEmptyLabel("Bạn chưa đăng nhập."));
            updateSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            refreshCartButtonText();
            return;
        }

        List<OrderDetail> items = cartService.getCartItems(customer);

        if (items.isEmpty()) {
            cartItemsContainer.getChildren().add(createEmptyLabel("Giỏ hàng của bạn đang trống."));
        } else {
            for (int i = 0; i < items.size(); i++) {
                HBox row = createCartItemRow(items.get(i));
                cartItemsContainer.getChildren().add(row);
                animateRowEntrance(row, i);
            }
        }

        BigDecimal subtotal = cartService.getSubtotal(customer);
        BigDecimal shipping = cartService.getShipping(customer);
        BigDecimal total = cartService.getTotal(customer);

        updateSummary(subtotal, shipping, total);
        refreshCartButtonText();
    }

    private HBox createCartItemRow(OrderDetail item) {
        VBox productCard = new VBox(8);
        productCard.setAlignment(Pos.CENTER);
        productCard.setPrefWidth(155);
        productCard.setPrefHeight(125);
        productCard.setStyle("-fx-background-color: #f5dce2; -fx-padding: 10; -fx-background-radius: 8;");

        ImageView imageView = new ImageView(loadProductImage(item.getProduct().getImage()));
        imageView.setFitWidth(78);
        imageView.setFitHeight(78);
        imageView.setPreserveRatio(false);

        Label nameLabel = new Label(item.getProduct().getProductName());
        nameLabel.setStyle("-fx-text-fill: #9b6666; -fx-font-size: 12px;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(125);
        nameLabel.setAlignment(Pos.CENTER);

        productCard.getChildren().addAll(imageView, nameLabel);

        BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        Label priceLabel = new Label( formatMoney(lineTotal) + " VND");
        priceLabel.setPrefWidth(180);
        priceLabel.setMinWidth(180);
        priceLabel.setMaxWidth(180);
        priceLabel.setAlignment(Pos.CENTER_LEFT);
        priceLabel.setStyle("-fx-text-fill: #9b6666; -fx-font-size: 28px;");


        Button btnMinus = new Button("-");
        String minusBaseStyle = "-fx-background-color: transparent; "
                + "-fx-text-fill: #8e5f5f; "
                + "-fx-font-size: 20px; "
                + "-fx-font-weight: bold; "
                + "-fx-cursor: hand; "
                + "-fx-padding: 0;";
        btnMinus.setStyle(minusBaseStyle);
        btnMinus.setMinSize(32, 32);
        btnMinus.setPrefSize(32, 32);
        btnMinus.setMaxSize(32, 32);
        applyButtonHover(btnMinus, minusBaseStyle, minusBaseStyle + "-fx-text-fill: #cf4f84;");
        btnMinus.setOnAction(e -> {
            cartService.decreaseQuantity(item.getOrderDetailId());
            loadCart();
        });

        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 20px; -fx-font-weight: bold;");
        quantityLabel.setMinWidth(40);
        quantityLabel.setPrefWidth(40);
        quantityLabel.setMaxWidth(40);
        quantityLabel.setAlignment(Pos.CENTER);

        Button btnPlus = new Button("+");
        String plusBaseStyle = "-fx-background-color: transparent; "
                + "-fx-text-fill: #8e5f5f; "
                + "-fx-font-size: 20px; "
                + "-fx-font-weight: bold; "
                + "-fx-cursor: hand; "
                + "-fx-padding: 0;";
        btnPlus.setStyle(plusBaseStyle);
        btnPlus.setMinSize(32, 32);
        btnPlus.setPrefSize(32, 32);
        btnPlus.setMaxSize(32, 32);
        applyButtonHover(btnPlus, plusBaseStyle, plusBaseStyle + "-fx-text-fill: #cf4f84;");
        btnPlus.setOnAction(e -> {
            try {
                cartService.increaseQuantity(item.getOrderDetailId());
                loadCart();
            } catch (Exception ex) {
                showInfo("Lỗi", ex.getMessage());
            }
        });

        HBox quantityBox = new HBox();
        quantityBox.setSpacing(12);
        quantityBox.setAlignment(Pos.CENTER);
        quantityBox.setPrefWidth(150);
        quantityBox.setMinWidth(150);
        quantityBox.setMaxWidth(150);
        quantityBox.setPrefHeight(50);
        quantityBox.setStyle("-fx-background-color: #edd6d8; -fx-background-radius: 8;");
        quantityBox.getChildren().addAll(btnMinus, quantityLabel, btnPlus);

        VBox noteBox = new VBox(6);
        noteBox.setAlignment(Pos.CENTER_LEFT);
        noteBox.setPrefWidth(320);
        noteBox.setMinWidth(320);
        noteBox.setMaxWidth(320);
        noteBox.setPadding(new Insets(6, 0, 6, 0));

        Label noteTitleLabel = new Label("Note:");
        noteTitleLabel.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 15px; -fx-font-weight: 700;");

        String noteText = item.getNote() == null || item.getNote().isBlank()
                ? "No note"
                : item.getNote().trim();

        Label noteLabel = new Label(noteText);
        noteLabel.setWrapText(true);
        noteLabel.setMaxWidth(320);
        noteLabel.setStyle("-fx-text-fill: #cf4f84; -fx-font-size: 14px;");

        noteBox.getChildren().addAll(noteTitleLabel, noteLabel);

        Button btnRemove = new Button("Remove");
        String removeBaseStyle = "-fx-text-fill: #8e5f5f; -fx-underline: true; -fx-background-color: transparent; -fx-font-size: 22px; -fx-cursor: hand;";
        String removeHoverStyle = "-fx-text-fill: #cf4f84; -fx-underline: true; -fx-background-color: transparent; -fx-font-size: 22px; -fx-cursor: hand;";
        btnRemove.setStyle(removeBaseStyle);
        applyButtonHover(btnRemove, removeBaseStyle, removeHoverStyle);
        btnRemove.setOnAction(e -> {
            cartService.removeItem(item.getOrderDetailId());
            loadCart();
        });

        HBox row = new HBox(55, productCard, priceLabel, quantityBox, noteBox, btnRemove);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 12, 12));
        row.setStyle("-fx-background-color: transparent; -fx-background-radius: 12;");
        applyRowHover(row);

        return row;
    }

    private void animateRowEntrance(HBox row, int index) {
        row.setOpacity(0);
        row.setTranslateY(12);

        FadeTransition fade = new FadeTransition(Duration.millis(180), row);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(180), row);
        slide.setFromY(12);
        slide.setToY(0);

        ParallelTransition show = new ParallelTransition(fade, slide);

        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(index * 70L));
        SequentialTransition sequence = new SequentialTransition(delay, show);
        sequence.play();
    }

    private void applyRowHover(HBox row) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(140), row);
        scaleUp.setToX(1.02);
        scaleUp.setToY(1.02);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(140), row);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        String normalStyle = "-fx-background-color: transparent; -fx-background-radius: 12;";
        String hoverStyle = "-fx-background-color: #fdf4f6; -fx-background-radius: 12;";

        row.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
            row.setStyle(hoverStyle);
        });

        row.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
            row.setStyle(normalStyle);
        });
    }

    private void applyButtonHover(Button button, String normalStyle, String hoverStyle) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), button);
        scaleUp.setToX(1.08);
        scaleUp.setToY(1.08);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(120), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
            button.setStyle(hoverStyle);
        });

        button.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
            button.setStyle(normalStyle);
        });
    }

    private Label createEmptyLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 22px; -fx-font-weight: 700;");
        return label;
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

    private void updateSummary(BigDecimal subtotal, BigDecimal shipping, BigDecimal total) {
        lblSubtotal.setText("Subtotal: " + formatMoney(subtotal) + " VND");
        lblShipping.setText("Shipping: " + formatMoney(shipping) + " VND");
        lblTotal.setText("Total: " + formatMoney(total) + " VND");
    }

    private String formatMoney(BigDecimal amount) {
        return moneyFormat.format(amount);
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
    public void goCheckout() {
        Customer customer = SessionManager.getCurrentCustomer();
        if (customer == null || cartService.getCartItems(customer).isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cart");
            alert.setHeaderText(null);
            alert.setContentText("Giỏ hàng đang trống.");
            alert.showAndWait();
            return;
        }

        SceneManager.switchScene("/fxml/Checkout.fxml", "Checkout");
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