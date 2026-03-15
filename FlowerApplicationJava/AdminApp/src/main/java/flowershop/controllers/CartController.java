package flowershop.controllers;

import flowershop.models.Customer;
import flowershop.models.OrderDetail;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
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

    private final CartService cartService = new CartService();
    private final DecimalFormat moneyFormat = new DecimalFormat("0.##");

    @FXML
    public void initialize() {
        loadCart();
    }

    private void loadCart() {
        cartItemsContainer.getChildren().clear();

        Customer customer = SessionManager.getCurrentCustomer();
        if (customer == null) {
            cartItemsContainer.getChildren().add(createEmptyLabel("Bạn chưa đăng nhập."));
            updateSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            return;
        }

        List<OrderDetail> items = cartService.getCartItems(customer);

        if (items.isEmpty()) {
            cartItemsContainer.getChildren().add(createEmptyLabel("Giỏ hàng của bạn đang trống."));
        } else {
            for (OrderDetail item : items) {
                cartItemsContainer.getChildren().add(createCartItemRow(item));
            }
        }

        BigDecimal subtotal = cartService.getSubtotal(customer);
        BigDecimal shipping = cartService.getShipping(customer);
        BigDecimal total = cartService.getTotal(customer);

        updateSummary(subtotal, shipping, total);
    }

    private HBox createCartItemRow(OrderDetail item) {
        VBox productCard = new VBox(8);
        productCard.setAlignment(Pos.CENTER);
        productCard.setPrefWidth(135);
        productCard.setPrefHeight(118);
        productCard.setStyle("-fx-background-color: #f5dce2; -fx-padding: 10;");

        ImageView imageView = new ImageView(loadProductImage(item.getProduct().getImage()));
        imageView.setFitWidth(78);
        imageView.setFitHeight(78);
        imageView.setPreserveRatio(false);

        Label nameLabel = new Label(item.getProduct().getProductName());
        nameLabel.setStyle("-fx-text-fill: #9b6666; -fx-font-size: 12px;");
        nameLabel.setWrapText(true);

        productCard.getChildren().addAll(imageView, nameLabel);

        BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        Label priceLabel = new Label("$" + formatMoney(lineTotal));
        priceLabel.setStyle("-fx-text-fill: #9b6666; -fx-font-size: 28px;");

        Button btnMinus = new Button("-");
        btnMinus.setStyle("-fx-background-color: transparent; -fx-text-fill: #8e5f5f; -fx-font-size: 20px; -fx-cursor: hand;");
        btnMinus.setOnAction(e -> {
            cartService.decreaseQuantity(item.getOrderDetailId());
            loadCart();
        });

        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 20px;");
        quantityLabel.setMinWidth(30);
        quantityLabel.setPrefWidth(40);
        quantityLabel.setAlignment(Pos.CENTER);

        Button btnPlus = new Button("+");
        btnPlus.setStyle("-fx-background-color: transparent; -fx-text-fill: #8e5f5f; -fx-font-size: 20px; -fx-cursor: hand;");
        btnPlus.setOnAction(e -> {
            cartService.increaseQuantity(item.getOrderDetailId());
            loadCart();
        });

        HBox quantityBox = new HBox(12, btnMinus, quantityLabel, btnPlus);
        quantityBox.setAlignment(Pos.CENTER);
        quantityBox.setPrefWidth(140);
        quantityBox.setPrefHeight(38);
        quantityBox.setStyle("-fx-background-color: #edd6d8;");

        Button btnRemove = new Button("Remove");
        btnRemove.setStyle("-fx-text-fill: #8e5f5f; -fx-underline: true; -fx-background-color: transparent; -fx-font-size: 22px; -fx-cursor: hand;");
        btnRemove.setOnAction(e -> {
            cartService.removeItem(item.getOrderDetailId());
            loadCart();
        });

        HBox row = new HBox(90, productCard, priceLabel, quantityBox, btnRemove);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0, 0, 10, 0));

        return row;
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
        lblSubtotal.setText("Subtotal: $" + formatMoney(subtotal));
        lblShipping.setText("Shipping: $" + formatMoney(shipping));
        lblTotal.setText("Total: $" + formatMoney(total));
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
}