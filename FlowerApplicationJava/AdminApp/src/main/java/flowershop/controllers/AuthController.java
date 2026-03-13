package flowershop.controllers;

import flowershop.dao.UserDAO;
import flowershop.models.User;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager; // Đã thêm import Session
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AuthController {

    @FXML
    private TextField txtLoginUsername;

    @FXML
    private PasswordField txtLoginPassword;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    void handleLogin(ActionEvent event) {
        String username = txtLoginUsername.getText();
        String password = txtLoginPassword.getText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ tài khoản và mật khẩu!");
            return;
        }

        User loggedInUser = userDAO.login(username, password);

        if (loggedInUser != null) {
            // ==================================================
            // LƯU TÀI KHOẢN VÀO SESSION Ở ĐÂY CHO DASHBOARD DÙNG
            SessionManager.setCurrentUser(loggedInUser);
            // ==================================================

            String role = loggedInUser.getRole();

            if ("admin".equals(role) || "staff".equals(role)) {
                SceneManager.switchScene("/fxml/AdminDashboard.fxml", "Admin Dashboard");
            } else if ("customer".equals(role)) {
                SceneManager.switchScene("/fxml/UserHome.fxml", "Flower Shop - Trang Chủ");
            } else {
                showAlert("Tài khoản của bạn chưa được phân quyền hợp lệ!");
            }
        } else {
            showAlert("Sai tài khoản hoặc mật khẩu.");
        }
    }

    @FXML
    void goToSignup(MouseEvent event) {
        try {
            SceneManager.switchScene("/fxml/sign-up.fxml", "Đăng ký tài khoản");
        } catch (Exception e) {
            System.out.println("Chưa có màn hình đăng ký: " + e.getMessage());
        }
    }

    @FXML
    void handleSignup(ActionEvent event) {
        showAlert("Chức năng đăng ký đang được hoàn thiện!");
    }

    @FXML
    void goToLogin(MouseEvent event) {
        try {
            SceneManager.switchScene("/fxml/login.fxml", "Đăng nhập");
        } catch (Exception e) {
            System.out.println("Lỗi chuyển về màn hình đăng nhập: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // =========================================================
    // HÀM LOGOUT DÙNG CHUNG CHO CẢ APP
    // =========================================================
    public static void logout() {
        try {
            SceneManager.switchScene("/fxml/login.fxml", "Đăng nhập");
        } catch (Exception e) {
            System.out.println("Lỗi chuyển màn hình đăng xuất: " + e.getMessage());
        }
    }
}