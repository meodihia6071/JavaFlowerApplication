package flowershop.controllers;

import flowershop.dao.OrderDAO;
import flowershop.dao.CustomerDAO;
import flowershop.models.Customer;
import flowershop.models.OrderDetail;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.services.PaymentService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import java.awt.Desktop;
import java.net.URI;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class OrderController {

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextArea txtAddress;

    @FXML
    private CheckBox chkCreditCard;

    @FXML
    private CheckBox chkPayPal;

    @FXML
    private CheckBox chkCashOnDelivery;

    @FXML
    private VBox orderItemsContainer;

    @FXML
    private Label lblSubtotal;

    @FXML
    private Label lblShipping;

    @FXML
    private Label lblDiscount;

    @FXML
    private Label lblTotal;

    @FXML
    private Label lblEarnedPoints;

    @FXML
    private Label lblCurrentPoints;

    @FXML
    private Label lblUsedPoints;

    @FXML
    private Button btnMinusPoint;

    @FXML
    private Button btnPlusPoint;

    @FXML
    private Button btnPlaceOrder;

    @FXML
    private Button btnCart;

    @FXML
    private VBox leftPanel;

    @FXML
    private VBox rightPanel;

    @FXML
    private Label lblCheckoutTitle;

    @FXML
    public void goProfile() {
        SceneManager.switchScene("/fxml/Profile.fxml", "Profile");
    }


    private boolean isAutoFilling = false;

    private final CartService cartService = new CartService();
    private final DecimalFormat moneyFormat = new DecimalFormat("0.##");

    private int usedPoints = 0;
    private Customer currentCustomer;

    @FXML
    public void initialize() {
        setupPhoneValidation();
        currentCustomer = SessionManager.getCurrentCustomer();

        loadCustomerInfo();
        loadOrderSummary();
        refreshCartButtonText();
        refreshPointsArea();
        playEntrance();

        addHoverEffect(btnPlaceOrder);
        addHoverEffect(btnMinusPoint);
        addHoverEffect(btnPlusPoint);
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            autoFillCustomer();
        });

        txtPhone.textProperty().addListener((obs, oldVal, newVal) -> {
            autoFillCustomer();
        });
    }

    private void handleVNPayPayment(long amount, int orderId) {
        try {
            String url = PaymentService.createQRUrl(amount, orderId);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoFillCustomer() {
        if (isAutoFilling) return;

        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        boolean validEmail = email.contains("@") && email.contains(".");
        boolean validPhone = phone.matches("\\d{10}");

        CustomerDAO dao = new CustomerDAO();
        Customer found = null;

        if (validEmail) {
            found = dao.findByEmail(email);
        } else if (validPhone) {
            found = dao.findByPhone(phone);
        }


    }

    private void setupPhoneValidation() {
        UnaryOperator<TextFormatter.Change> phoneFilter = change -> {
            String newText = change.getControlNewText();

            if (!newText.matches("\\d*")) {
                return null;
            }

            if (newText.length() > 10) {
                return null;
            }

            return change;
        };

        txtPhone.setTextFormatter(new TextFormatter<>(phoneFilter));
    }

    private void loadCustomerInfo() {
        if (currentCustomer == null) return;

        txtFullName.setText(currentCustomer.getCustomerName() == null ? "" : currentCustomer.getCustomerName());
        txtEmail.setText(currentCustomer.getEmail() == null ? "" : currentCustomer.getEmail());
        txtPhone.setText(sanitizePhone(currentCustomer.getPhone()));

        if(currentCustomer.getCustomerName() != null && !currentCustomer.getCustomerName().isBlank()) {
            txtFullName.setDisable(true);
        }
        if (currentCustomer.getEmail() != null && !currentCustomer.getEmail().isBlank()) {
            txtEmail.setDisable(true);
        }

        if (currentCustomer.getPhone() != null && !currentCustomer.getPhone().isBlank()) {
            txtPhone.setDisable(true);
        }
    }

    private String sanitizePhone(String phone) {
        if (phone == null) return "";
        String digitsOnly = phone.replaceAll("\\D", "");
        return digitsOnly.length() > 10 ? digitsOnly.substring(0, 10) : digitsOnly;
    }

    private void loadOrderSummary() {
        orderItemsContainer.getChildren().clear();

        if (currentCustomer == null) {
            setMoneyLabels(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            lblEarnedPoints.setText("+0");
            btnPlaceOrder.setDisable(true);
            return;
        }

        List<OrderDetail> items = cartService.getCartItems(currentCustomer);

        if (items.isEmpty()) {
            Label empty = new Label("Your cart is empty.");
            empty.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 15px; -fx-font-weight: 700;");
            orderItemsContainer.getChildren().add(empty);

            setMoneyLabels(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            lblEarnedPoints.setText("+0");
            btnPlaceOrder.setDisable(true);
            return;
        }

        for (OrderDetail item : items) {
            String itemName = item.getProduct().getProductName() + " x" + item.getQuantity();
            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            Label nameLabel = new Label(itemName);
            nameLabel.setPrefWidth(220);
            nameLabel.setWrapText(true);
            nameLabel.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 16px;");

            Label priceLabel = new Label(formatMoney(lineTotal) + " VND");
            priceLabel.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 16px;");

            HBox row = new HBox(nameLabel, priceLabel);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setSpacing(8);

            orderItemsContainer.getChildren().add(row);
        }

        btnPlaceOrder.setDisable(false);
        updatePriceSummary();
    }

    private void updatePriceSummary() {
        if (currentCustomer == null) {
            setMoneyLabels(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            lblEarnedPoints.setText("+0");
            return;
        }

        BigDecimal subtotal = cartService.getSubtotal(currentCustomer);
        BigDecimal shipping = cartService.getShipping(currentCustomer);
        BigDecimal discount = cartService.getDiscountAmount(currentCustomer, usedPoints);
        BigDecimal finalTotal = cartService.getTotalAfterPoints(currentCustomer, usedPoints);
        int earnedPoints = cartService.calculateEarnedPoints(currentCustomer, usedPoints);

        setMoneyLabels(subtotal, shipping, discount, finalTotal);
        lblEarnedPoints.setText("+" + earnedPoints);
    }

    private void setMoneyLabels(BigDecimal subtotal, BigDecimal shipping, BigDecimal discount, BigDecimal total) {
        lblSubtotal.setText(formatMoney(subtotal)+ "VND");
        lblShipping.setText(formatMoney(shipping)+ "VND");
        lblDiscount.setText("-" +formatMoney(discount)+ "VND");
        lblTotal.setText(formatMoney(total)+ "VND");
    }

    private void refreshPointsArea() {
        int currentPoints = currentCustomer == null ? 0 : currentCustomer.getPoints();

        lblCurrentPoints.setText("Current points: " + currentPoints + " (max: " + getMaxUsablePoints() + ")");
        lblUsedPoints.setText(String.valueOf(usedPoints));

        btnMinusPoint.setDisable(usedPoints <= 0);
        btnPlusPoint.setDisable(currentCustomer == null || usedPoints >= getMaxUsablePoints());

        updatePriceSummary();
    }

    @FXML
    private void handleIncreasePoint() {
        if (currentCustomer == null) return;

        int maxUsable = getMaxUsablePoints();

        if (usedPoints < maxUsable) {
            usedPoints++;
            refreshPointsArea();
        }
    }

    @FXML
    private void handleDecreasePoint() {
        if (usedPoints > 0) {
            usedPoints--;
            refreshPointsArea();
        }
    }

    @FXML
    private void handlePaymentSelection() {
        if (chkCreditCard.isSelected()) {
            chkPayPal.setSelected(false);
            chkCashOnDelivery.setSelected(false);
        } else if (chkPayPal.isSelected()) {
            chkCreditCard.setSelected(false);
            chkCashOnDelivery.setSelected(false);
        } else if (chkCashOnDelivery.isSelected()) {
            chkCreditCard.setSelected(false);
            chkPayPal.setSelected(false);
        }
    }

    @FXML
    private void handlePlaceOrder() {
        if (currentCustomer == null) {
            showInfo("Lỗi", "Bạn cần đăng nhập trước.");
            return;
        }

        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String paymentMethod = getSelectedPaymentMethod();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            showInfo("Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin nhận hàng.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showInfo("Email chưa hợp lệ", "Vui lòng nhập đúng định dạng email.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            showInfo("Số điện thoại chưa hợp lệ", "Số điện thoại phải gồm đúng 10 chữ số.");
            return;
        }

        if (paymentMethod == null) {
            showInfo("Chưa chọn thanh toán", "Vui lòng chọn 1 phương thức thanh toán.");
            return;
        }
        CustomerDAO dao = new CustomerDAO();

        Customer existingEmail = dao.findByEmail(email);
        if (existingEmail != null && existingEmail.getCustomerId() != currentCustomer.getCustomerId()) {
            showInfo("Lỗi", "Email đã tồn tại!");
            return;
        }

        Customer existingPhone = dao.findByPhone(phone);
        if (existingPhone != null && existingPhone.getCustomerId() != currentCustomer.getCustomerId()) {
            showInfo("Lỗi", "Số điện thoại đã tồn tại!");
            return;
        }

        currentCustomer.setCustomerName(fullName);
        currentCustomer.setEmail(email);
        currentCustomer.setPhone(phone);

        dao.update(currentCustomer);
        try {
            var order = cartService.placeOrder(
                    currentCustomer,
                    fullName,
                    email,
                    phone,
                    address,
                    paymentMethod,
                    usedPoints
            );

            if (paymentMethod.equals("VNPay")) {
                handleVNPayPayment(order.getTotal().longValue(), order.getOrderId());
                showInfo("Đang xử lý", "Đang chờ xác nhận thanh toán...");

                new Thread(() -> {
                    try {
                        Thread.sleep(10000);

                        javafx.application.Platform.runLater(() -> {
                            order.setStatus("PAID");
                            new OrderDAO().update(order);

                            showInfo("Thành công", "Thanh toán thành công!");
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        }
                    }).start();
                }
            else {
                showInfo("Thành công", "Thanh toán thành công! ");
            }

            SceneManager.switchScene("/fxml/UserHome.fxml", "User Home");

        } catch (Exception e) {
            showInfo("Lỗi", e.getMessage());
        }
    }

    private String getSelectedPaymentMethod() {
        if (chkCreditCard.isSelected()) return "Credit Card";
        if (chkPayPal.isSelected()) return "VNPay";
        if (chkCashOnDelivery.isSelected()) return "Thanh toán khi nhận hàng";
        return null;
    }

    private void refreshCartButtonText() {
        if (btnCart == null) return;

        if (currentCustomer == null) {
            btnCart.setText("Cart");
            return;
        }

        int count = cartService.getCartQuantity(currentCustomer);
        btnCart.setText("Cart (" + count + ")");
    }

    private void playEntrance() {
        animateNode(lblCheckoutTitle, 0, -18, 70);
        animateNode(leftPanel, 0, 28, 150);
        animateNode(rightPanel, 0, 28, 240);
    }

    private void animateNode(Node node, double fromX, double fromY, int delayMillis) {
        node.setOpacity(0);
        node.setTranslateX(fromX);
        node.setTranslateY(fromY);

        FadeTransition fade = new FadeTransition(Duration.millis(420), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(420), node);
        slide.setFromX(fromX);
        slide.setFromY(fromY);
        slide.setToX(0);
        slide.setToY(0);

        ParallelTransition parallel = new ParallelTransition(fade, slide);
        parallel.setDelay(Duration.millis(delayMillis));
        parallel.play();
    }

    private void addHoverEffect(Button button) {
        if (button == null) return;

        DropShadow shadow = new DropShadow();
        shadow.setRadius(18);
        shadow.setOffsetY(5);
        shadow.setColor(Color.rgb(201, 126, 160, 0.28));

        button.setOnMouseEntered(e -> {
            button.setEffect(shadow);

            ScaleTransition scale = new ScaleTransition(Duration.millis(160), button);
            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);

            ScaleTransition scale = new ScaleTransition(Duration.millis(160), button);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        });
    }

    private int getMaxUsablePoints() {
        int currentPoints = currentCustomer != null ? currentCustomer.getPoints() : 0;
        return Math.min(20, currentPoints);
    }

    private String formatMoney(BigDecimal amount) {
        return moneyFormat.format(amount == null ? BigDecimal.ZERO : amount);
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

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}