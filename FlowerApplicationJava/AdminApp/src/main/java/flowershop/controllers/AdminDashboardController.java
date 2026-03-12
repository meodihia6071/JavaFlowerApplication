package flowershop.controllers;

import flowershop.services.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class AdminDashboardController {

    @FXML
    private ImageView avatarImageView;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtRole;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnLogout;

    @FXML
    public void initialize() {
        // Khởi tạo dữ liệu khi load màn hình (Sẽ làm sau)
    }

    @FXML
    void handleUpdateProfile(ActionEvent event) {
        showComingSoonAlert("Cập nhật thông tin cá nhân");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        // Đã thêm tham số thứ 2 là "Đăng nhập"
        try {
            SceneManager.switchScene("/fxml/login.fxml", "Đăng nhập");
        } catch (Exception e) {
            e.printStackTrace();
            showComingSoonAlert("Lỗi chuyển màn hình đăng xuất");
        }
    }

    // ================= CÁC NÚT TÍNH NĂNG CHÍNH =================

    @FXML
    void handleProducts(ActionEvent event) {
        showComingSoonAlert("Quản lý Sản phẩm (Products)");
    }

    @FXML
    void handleCategories(ActionEvent event) {
        showComingSoonAlert("Quản lý Danh mục (Categories)");
    }

    @FXML
    void handleOrders(ActionEvent event) {
        showComingSoonAlert("Quản lý Đơn hàng (Orders)");
    }

    @FXML
    void handleCustomers(ActionEvent event) {
        showComingSoonAlert("Quản lý Khách hàng (Customers)");
    }

    @FXML
    void handleStock(ActionEvent event) {
        showComingSoonAlert("Quản lý Kho (Stock)");
    }

    @FXML
    void handleSuppliers(ActionEvent event) {
        showComingSoonAlert("Quản lý Nhà cung cấp (Suppliers)");
    }

    @FXML
    void handleReports(ActionEvent event) {
        showComingSoonAlert("Xem Báo cáo (Reports)");
    }

    @FXML
    void handleUserManage(ActionEvent event) {
        showComingSoonAlert("Quản lý Tài khoản (User Manage)");
    }

    // ================= HÀM TIỆN ÍCH =================

    private void showComingSoonAlert(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo hệ thống");
        alert.setHeaderText("Chức năng đang được phát triển");
        alert.setContentText("Tính năng [" + featureName + "] chưa được thiết lập. Vui lòng quay lại sau!");
        alert.showAndWait();
    }
}