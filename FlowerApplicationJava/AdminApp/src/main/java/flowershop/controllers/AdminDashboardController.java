package flowershop.controllers;

import flowershop.models.User;
import flowershop.models.Customer;
import flowershop.dao.CustomerDAO;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class AdminDashboardController {

    @FXML private TextField txtEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtRole;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnTogglePassword;

    @FXML private Button btnUpdate;
    @FXML private Button btnLogout;

    @FXML private GridPane buttonGrid;

    // HÀM CHẠY NGAY KHI MỞ MÀN HÌNH - SETUP HIỆU ỨNG VÀ ĐỔ DỮ LIỆU THẬT
    @FXML
    public void initialize() {
        // 1. Phục hồi toàn bộ hiệu ứng phóng to cho các nút
        if (btnUpdate != null) addSmoothHoverEffect(btnUpdate);
        if (btnLogout != null) addSmoothHoverEffect(btnLogout);
        if (buttonGrid != null) {
            for (Node node : buttonGrid.getChildren()) {
                if (node instanceof Button) addSmoothHoverEffect((Button) node);
            }
        }

        // 2. LẤY DỮ LIỆU TỪ BẢNG USERS
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            txtRole.setText(currentUser.getRole());
            txtPassword.setText(currentUser.getPassword());

            // 3. CẦM user_id CHẠY SANG BẢNG CUSTOMERS ĐỂ LẤY EMAIL VÀ TÊN
            try {
                CustomerDAO customerDAO = new CustomerDAO();
                Customer profile = customerDAO.findByUserId(currentUser.getUserId());

                if (profile != null) {
                    txtName.setText(profile.getCustomerName());
                    if (profile.getEmail() != null) {
                        txtEmail.setText(profile.getEmail());
                    } else {
                        txtEmail.setText("");
                    }
                } else {
                    txtName.setText(currentUser.getUsername());
                    txtEmail.setText("Chưa liên kết hồ sơ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Lỗi khi kéo dữ liệu từ bảng customers sang!");
            }
        }
    }

    // HÀM TẠO HIỆU ỨNG PHÓNG TO NÚT
    private void addSmoothHoverEffect(Button btn) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), btn);
        scaleIn.setToX(1.03);
        scaleIn.setToY(1.03);

        ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.3), btn);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            scaleOut.stop();
            scaleIn.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            scaleIn.stop();
            scaleOut.playFromStart();
        });
    }

    // --- CÁC HÀM XỬ LÝ SỰ KIỆN GIAO DIỆN ---

    @FXML
    public void handleUpdateProfile(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (email == null || email.trim().isEmpty()) {
            showAlert("Lỗi", "Email không được để trống!");
            return;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        if (!email.matches(emailRegex)) {
            showAlert("Lỗi", "Định dạng email không hợp lệ!\nVui lòng nhập đúng định dạng (Ví dụ: quyenha@gmail.com)");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showAlert("Lỗi", "Mật khẩu không được để trống!");
            return;
        }

        showAlert("Thành công", "Dữ liệu hợp lệ! Sẵn sàng gọi code Database để cập nhật.");
    }

    @FXML
    public void togglePasswordVisibility(ActionEvent event) {
        if (txtPassword.isVisible()) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            btnTogglePassword.setText("Ẩn");
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            btnTogglePassword.setText("Hiện");
        }
    }

    // =========================================================
    // NÚT ĐĂNG XUẤT SẠCH SẼ (GỌI SCENEMANAGER)
    // =========================================================
    @FXML
    public void handleLogout(ActionEvent event) {
        SessionManager.clear();
        SceneManager.switchScene("/fxml/login.fxml", "Đăng nhập");
    }

    // =========================================================
    // BỘ ĐIỀU HƯỚNG MỚI (DÙNG CHO SIDEBAR MENU TRÁI)
    // =========================================================
    @FXML public void goDashboard(ActionEvent event) { SceneManager.switchScene("/fxml/AdminDashboard.fxml", "Dashboard"); }
    @FXML public void goProducts(ActionEvent event) { SceneManager.switchScene("/fxml/AdminProducts.fxml", "Products"); }
    @FXML public void goCategories(ActionEvent event) { SceneManager.switchScene("/fxml/AdminCategories.fxml", "Categories"); }
    @FXML public void goOrders(ActionEvent event) { SceneManager.switchScene("/fxml/AdminOrders.fxml", "Orders"); }
    @FXML public void goCustomers(ActionEvent event) { SceneManager.switchScene("/fxml/AdminCustomers.fxml", "Customers"); }
    @FXML public void goSuppliers(ActionEvent event) { SceneManager.switchScene("/fxml/AdminSuppliers.fxml", "Suppliers"); }
    @FXML public void goStock(ActionEvent event) { SceneManager.switchScene("/fxml/AdminStock.fxml", "Stock"); }
    @FXML public void goReports(ActionEvent event) { SceneManager.switchScene("/fxml/AdminReports.fxml", "Reports"); }

    // Gộp chung Employees và UserManage làm 1 trang để tránh lỗi FXML gọi nhầm
    @FXML public void goEmployees(ActionEvent event) { SceneManager.switchScene("/fxml/AdminEmployees.fxml", "Employees"); }
    @FXML public void goUserManage(ActionEvent event) { goEmployees(event); }

    // =========================================================
    // BỘ ĐIỀU HƯỚNG CŨ (DÙNG CHO CÁC NÚT TO Ở GIỮA DASHBOARD)
    // =========================================================
    @FXML public void handleCategories(ActionEvent event) { goCategories(event); }
    @FXML public void handleProducts(ActionEvent event) { goProducts(event); }
    @FXML public void handleOrders(ActionEvent event) { goOrders(event); }
    @FXML public void handleCustomers(ActionEvent event) { goCustomers(event); }
    @FXML public void handleStock(ActionEvent event) { goStock(event); }
    @FXML public void handleSuppliers(ActionEvent event) { goSuppliers(event); }
    @FXML public void handleUserManage(ActionEvent event) { goEmployees(event); }
    @FXML public void handleReports(ActionEvent event) { goReports(event); }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}