package flowershop.controllers;

import flowershop.dao.ProductDAO;
import javafx.scene.control.Label;
import flowershop.models.Category;
import flowershop.models.Customer;
import flowershop.models.Product;
import flowershop.services.CartService;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import flowershop.utils.HibernateUtil;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class UserHomeController {

    @FXML
    private TilePane categoryPane;

    @FXML
    private TilePane productPane;

    @FXML
    private Button btnCart;

    private final CartService cartService = new CartService();
    private final ProductDAO productDAO = new ProductDAO();

    @FXML
    public void initialize() {
        if (categoryPane != null) {
            loadRandomProducts();
            loadRandomCategories();
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

        Platform.runLater(() -> {
            applyHoverEffectsToContainer(categoryPane);
            applyHoverEffectsToContainer(productPane);
            wireFeaturedProductCards();
            refreshCartButtonText();
        });
    }
    private Image loadImage(String imageName) {
        try {
            if (imageName != null && !imageName.isBlank()) {
                var stream = getClass().getResourceAsStream("/images/" + imageName);
                if (stream != null) {
                    return new Image(stream);
                }
            }
        } catch (Exception ignored) {}

        return new Image(getClass().getResourceAsStream("/images/flower-rose.jpg"));
    }
    private void wireFeaturedProductCards() {
        if (productPane == null) return;

        for (Node node : productPane.getChildren()) {
            if (node instanceof VBox card) {
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
    }
    private void loadRandomProducts() {
        List<Product> products = productDAO.findRandomInStock(5);
        productPane.getChildren().clear();

        for (Product p : products) {
            productPane.getChildren().add(createProductCard(p));
        }
    }
    private void loadRandomCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Category> categories = session.createQuery(
                    "FROM Category ORDER BY rand()",
                    Category.class
            ).setMaxResults(5).list();

            categoryPane.getChildren().clear();

            for (Category c : categories) {
                categoryPane.getChildren().add(createCategoryCard(c));
            }
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

    private void refreshCartButtonText() {
        if (btnCart == null) return;

        Customer customer = SessionManager.getCurrentCustomer();
        int cartCount = cartService.getCartQuantity(customer);

        btnCart.setText(cartCount > 0 ? "Cart (" + cartCount + ")" : "Cart");
    }

    private void applyHoverEffectsToContainer(Parent root) {
        if (root == null) return;

        List<VBox> cards = new ArrayList<>();
        collectCardVBoxes(root, cards);

        for (VBox card : cards) {
            Labeled titleNode = findTitleNode(card);
            if (titleNode != null) {
                applyHoverEffect(card, titleNode);
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
    private VBox createProductCard(Product product) {

        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setPrefWidth(185);
        card.setPrefHeight(280);
        card.setStyle("-fx-background-color: #f5dce2; -fx-padding: 12;");

        ImageView img = new ImageView(loadImage(product.getImage()));
        img.setFitWidth(150);
        img.setFitHeight(150);

        Label name = new Label(product.getProductName());
        Label price = new Label("$" + product.getPrice());

        Button btn = new Button("Add to Cart");
        btn.setUserData(product.getProductName());
        btn.setOnAction(this::handleAddToCart);
        if (product.getQuantity() <= 0) {
            btn.setText("Out of stock");
            btn.setDisable(true);
        }
        card.getChildren().addAll(img, name, price, btn);

        return card;
    }
    private VBox createCategoryCard(Category category) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setStyle("-fx-background-color: #f5dce2; -fx-padding: 12;");

        ImageView img = new ImageView(new Image(getClass().getResourceAsStream("/images/category.jpg")));
        img.setFitWidth(150);
        img.setFitHeight(150);

        Button btn = new Button(category.getCategoryName());
        btn.setOnAction(this::handleCategoryClick);

        card.getChildren().addAll(img, btn);

        return card;
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
            Button source = (Button) event.getSource();
            String productName = source.getUserData() != null ? source.getUserData().toString().trim() : "";

            if (productName.isBlank()) {
                throw new IllegalArgumentException("Không xác định được sản phẩm.");
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