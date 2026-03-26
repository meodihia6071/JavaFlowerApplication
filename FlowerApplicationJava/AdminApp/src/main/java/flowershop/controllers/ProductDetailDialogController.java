package flowershop.controllers;

import flowershop.models.Customer;
import flowershop.models.Product;
import flowershop.services.CartService;
import flowershop.services.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ProductDetailDialogController {

    @FXML
    private StackPane overlayRoot;
    @FXML
    private Label lblStock;

    private int stock = 0;
    @FXML
    private BorderPane dialogCard;

    @FXML
    private ImageView imgProduct;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblName;

    @FXML
    private Label lblQuantity;

    @FXML
    private TextArea txtNote;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnAddToCart;

    @FXML
    private Button btnDecrease;

    @FXML
    private Button btnIncrease;

    @FXML
    private HBox quantityBox;

    private final CartService cartService = new CartService();
    private final DecimalFormat moneyFormat = new DecimalFormat("0.##");

    private Product product;
    private int quantity = 1;
    private Stage stage;
    private boolean addedToCart = false;
    private boolean closing = false;

    public static boolean showDialog(Product product, Window ownerWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ProductDetailDialogController.class.getResource("/fxml/ProductDetailDialog.fxml")
            );
            Parent root = loader.load();

            ProductDetailDialogController controller = loader.getController();
            controller.setProduct(product);

            Stage dialog = new Stage();
            dialog.initModality(Modality.WINDOW_MODAL);
            if (ownerWindow != null) {
                dialog.initOwner(ownerWindow);
            }
            dialog.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            dialog.setScene(scene);
            controller.setStage(dialog);

            dialog.showAndWait();
            return controller.addedToCart;

        } catch (Exception e) {
            throw new RuntimeException("Không mở được popup chi tiết sản phẩm.", e);
        }
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            playOpenAnimation();
            setupHoverAnimations();
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProduct(Product product) {
        this.stock = product.getQuantity();
        lblStock.setText("Stock: " + stock);
        this.product = product;
        if (stock <= 0) {
            btnAddToCart.setText("Out of stock");
            btnAddToCart.setDisable(true);

            btnIncrease.setDisable(true);
            btnDecrease.setDisable(true);
        } else {
            btnAddToCart.setText("Add to Cart");
            btnAddToCart.setDisable(false);

            btnIncrease.setDisable(false);
            btnDecrease.setDisable(false);
        }
        updateUI();
    }

    private void updateUI() {
        if (product == null) return;

        lblName.setText(product.getProductName());
        lblPrice.setText(formatMoney(product.getPrice()) + " VND");
        lblQuantity.setText(String.valueOf(quantity));
        imgProduct.setImage(loadProductImage(product.getImage()));
    }

    private void playOpenAnimation() {
        if (overlayRoot == null || dialogCard == null) return;

        overlayRoot.setOpacity(0);
        dialogCard.setOpacity(0);
        dialogCard.setScaleX(0.92);
        dialogCard.setScaleY(0.92);
        dialogCard.setTranslateY(-12);

        FadeTransition overlayFade = new FadeTransition(Duration.millis(180), overlayRoot);
        overlayFade.setFromValue(0);
        overlayFade.setToValue(1);

        FadeTransition cardFade = new FadeTransition(Duration.millis(200), dialogCard);
        cardFade.setFromValue(0);
        cardFade.setToValue(1);

        ScaleTransition cardScale = new ScaleTransition(Duration.millis(220), dialogCard);
        cardScale.setFromX(0.92);
        cardScale.setFromY(0.92);
        cardScale.setToX(1.0);
        cardScale.setToY(1.0);

        TranslateTransition cardSlide = new TranslateTransition(Duration.millis(220), dialogCard);
        cardSlide.setFromY(-12);
        cardSlide.setToY(0);

        ParallelTransition showAnimation = new ParallelTransition(
                overlayFade, cardFade, cardScale, cardSlide
        );
        showAnimation.play();
    }

    private void playCloseAnimation(Runnable afterClose) {
        if (closing) return;
        closing = true;

        FadeTransition overlayFade = new FadeTransition(Duration.millis(150), overlayRoot);
        overlayFade.setFromValue(overlayRoot.getOpacity());
        overlayFade.setToValue(0);

        FadeTransition cardFade = new FadeTransition(Duration.millis(150), dialogCard);
        cardFade.setFromValue(dialogCard.getOpacity());
        cardFade.setToValue(0);

        ScaleTransition cardScale = new ScaleTransition(Duration.millis(150), dialogCard);
        cardScale.setToX(0.94);
        cardScale.setToY(0.94);

        TranslateTransition cardSlide = new TranslateTransition(Duration.millis(150), dialogCard);
        cardSlide.setToY(10);

        ParallelTransition hideAnimation = new ParallelTransition(
                overlayFade, cardFade, cardScale, cardSlide
        );
        hideAnimation.setOnFinished(e -> {
            if (afterClose != null) {
                afterClose.run();
            }
        });
        hideAnimation.play();
    }

    private void setupHoverAnimations() {
        applyButtonHover(btnClose, 1.12);
        applyButtonHover(btnCancel, 1.05);
        applyButtonHover(btnAddToCart, 1.05);
        applyButtonHover(btnDecrease, 1.10);
        applyButtonHover(btnIncrease, 1.10);
        applyImageHover(imgProduct);
        applyQuantityBoxHover(quantityBox);
    }

    private void applyButtonHover(Button button, double hoverScale) {
        if (button == null) return;

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), button);
        scaleUp.setToX(hoverScale);
        scaleUp.setToY(hoverScale);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(120), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });

        button.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });
    }

    private void applyImageHover(ImageView imageView) {
        if (imageView == null) return;

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(180), imageView);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(180), imageView);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        imageView.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });

        imageView.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });
    }

    private void applyQuantityBoxHover(HBox box) {
        if (box == null) return;

        String normalStyle = "-fx-background-color: #f3dfe3; -fx-background-radius: 10; -fx-padding: 10 14 10 14;";
        String hoverStyle = "-fx-background-color: #f7e8ec; -fx-background-radius: 10; -fx-padding: 10 14 10 14;";

        box.setOnMouseEntered(e -> box.setStyle(hoverStyle));
        box.setOnMouseExited(e -> box.setStyle(normalStyle));
    }

    @FXML
    private void handleIncrease() {
        int current = Integer.parseInt(lblQuantity.getText());

        if (current < stock) {
            lblQuantity.setText(String.valueOf(current + 1));
        }
    }

    @FXML
    private void handleDecrease() {
        int current = Integer.parseInt(lblQuantity.getText());

        if (current > 1) {
            lblQuantity.setText(String.valueOf(current - 1));
        }
    }

    private void animateQuantityChange() {
        ScaleTransition pop = new ScaleTransition(Duration.millis(120), lblQuantity);
        pop.setFromX(1.0);
        pop.setFromY(1.0);
        pop.setToX(1.18);
        pop.setToY(1.18);
        pop.setCycleCount(2);
        pop.setAutoReverse(true);
        pop.play();
    }

    @FXML
    private void handleAddToCart() {
        Customer customer = SessionManager.getCurrentCustomer();
        int quantity = Integer.parseInt(lblQuantity.getText());


        try {
            if (quantity > stock) {
                return;
            }
            if (product == null) {
                throw new IllegalArgumentException("Không xác định được sản phẩm.");
            }

            String note = txtNote.getText() == null ? "" : txtNote.getText().trim();
            cartService.addToCart(customer, product.getProductName(), quantity, note);
            addedToCart = true;
            playCloseAnimation(this::closeDialog);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClose() {
        playCloseAnimation(this::closeDialog);
    }

    private void closeDialog() {
        if (stage != null) {
            stage.close();
        }
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

    private String formatMoney(BigDecimal amount) {
        return moneyFormat.format(amount);
    }
}