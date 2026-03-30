package flowershop.controllers;

import flowershop.services.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ProfileController {

    @FXML
    private VBox contentArea;

    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private Button btnSaveProfile;

    @FXML
    private Button btnCart;

    @FXML
    private Button btnProfile;

    private List<Node> personalInfoContent;

    @FXML
    public void initialize() {
        personalInfoContent = new ArrayList<>(contentArea.getChildren());
        showPersonalInfo();
    }

    @FXML
    public void goHome() {
        SceneManager.switchScene("/fxml/UserHome.fxml", "Home");
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
    public void goProfile() {
        SceneManager.switchScene("/fxml/Profile.fxml", "Profile");
    }

    @FXML
    public void handleLogout() {
        AuthController.logout();
    }

    @FXML
    public void showPersonalInfo() {
        contentArea.getChildren().setAll(personalInfoContent);
    }

    @FXML
    public void showChangePassword() {
        Label title = new Label("ĐỔI MẬT KHẨU");
        title.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 26px; -fx-font-weight: 700;");

        VBox formBox = new VBox(18);
        formBox.setAlignment(javafx.geometry.Pos.CENTER);

        HBox row1 = new HBox(20);
        row1.setAlignment(javafx.geometry.Pos.CENTER);

        Label lbOld = new Label("Mật khẩu cũ");
        lbOld.setPrefWidth(170);
        lbOld.setMinWidth(170);
        lbOld.setMaxWidth(170);
        lbOld.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 20px; -fx-font-weight: 600;");

        TextField txtOld = new TextField();
        txtOld.setPromptText("Nhập mật khẩu cũ");
        txtOld.setPrefWidth(320);
        txtOld.setStyle("-fx-font-size: 18px;");
        txtOld.getStyleClass().add("profile-input");

        row1.getChildren().addAll(lbOld, txtOld);

        HBox row2 = new HBox(20);
        row2.setAlignment(javafx.geometry.Pos.CENTER);

        Label lbNew = new Label("Mật khẩu mới");
        lbNew.setPrefWidth(170);
        lbNew.setMinWidth(170);
        lbNew.setMaxWidth(170);
        lbNew.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 20px; -fx-font-weight: 600;");

        TextField txtNew = new TextField();
        txtNew.setPromptText("Nhập mật khẩu mới");
        txtNew.setPrefWidth(320);
        txtNew.setStyle("-fx-font-size: 18px;");
        txtNew.getStyleClass().add("profile-input");

        row2.getChildren().addAll(lbNew, txtNew);

        HBox row3 = new HBox(20);
        row3.setAlignment(javafx.geometry.Pos.CENTER);

        Label lbConfirm = new Label("Xác nhận");
        lbConfirm.setPrefWidth(170);
        lbConfirm.setMinWidth(170);
        lbConfirm.setMaxWidth(170);
        lbConfirm.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 20px; -fx-font-weight: 600;");

        TextField txtConfirm = new TextField();
        txtConfirm.setPromptText("Nhập lại mật khẩu");
        txtConfirm.setPrefWidth(320);
        txtConfirm.setStyle("-fx-font-size: 18px;");
        txtConfirm.getStyleClass().add("profile-input");

        row3.getChildren().addAll(lbConfirm, txtConfirm);

        formBox.getChildren().addAll(row1, row2, row3);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnConfirm = new Button("Xác nhận");
        btnConfirm.setPrefWidth(130);
        btnConfirm.setPrefHeight(38);
        btnConfirm.setStyle("-fx-font-size: 16px;");
        btnConfirm.getStyleClass().add("primary-button");

        buttonBox.getChildren().add(btnConfirm);

        contentArea.getChildren().setAll(title, formBox, buttonBox);
    }

    @FXML
    public void showDeliveryHistory() {
        VBox box = new VBox(18);
        box.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label("LỊCH SỬ GIAO HÀNG");
        title.setStyle("-fx-text-fill: #8e5f5f; -fx-font-size: 26px; -fx-font-weight: 700;");

        box.getChildren().addAll(title);
        contentArea.getChildren().setAll(box);
    }
}