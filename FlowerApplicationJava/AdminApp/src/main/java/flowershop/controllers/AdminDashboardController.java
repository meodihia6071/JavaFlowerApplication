package flowershop.controllers;

import flowershop.dao.UserDAO;
import flowershop.models.User;
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
    @FXML private TextField txtName; // Ô này giờ dùng để hiển thị Username
    @FXML private TextField txtRole;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnTogglePassword;

    @FXML private Button btnUpdate;
    @FXML private Button btnLogout;
    @FXML private GridPane buttonGrid;

    // ========================================================
    // 1. CHẠY NGAY KHI MỞ MÀN HÌNH DASHBOARD
    // ========================================================
    @FXML
    public void initialize() {
        // Tạo hiệu ứng hover cho các nút
        if (btnUpdate != null) addSmoothHoverEffect(btnUpdate);
        if (btnLogout != null) addSmoothHoverEffect(btnLogout);
        if (buttonGrid != null) {
            for (Node node : buttonGrid.getChildren()) {
                if (node instanceof Button) addSmoothHoverEffect((Button) node);
            }
        }

        // Kéo dữ liệu từ Session ra đổ lên Form
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            txtName.setText(currentUser.getUsername()); // Đổ Username
            txtEmail.setText(currentUser.getEmail());    // Đổ Email
            txtRole.setText(currentUser.getRole());      // Đổ Role
            txtPassword.setText(currentUser.getPassword());
        }
    }

    // ========================================================
    // 2. LƯU DỮ LIỆU CẬP NHẬT VÀO DATABASE (DÙNG USER DAO)
    // ========================================================
    @FXML
    public void handleUpdateProfile(ActionEvent event) {
        String newUsername = txtName.getText();
        String newEmail = txtEmail.getText();
        String newPassword = txtPassword.isVisible() ? txtPassword.getText() : txtPasswordVisible.getText();

        // 1. Kiểm tra không được để trống
        if (newUsername == null || newUsername.trim().isEmpty() ||
                newEmail == null || newEmail.trim().isEmpty() ||
                newPassword == null || newPassword.trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ Username, Email và Mật khẩu!");
            return;
        }

        // 2. Kiểm tra định dạng Email
        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            showAlert("Lỗi", "Định dạng email không hợp lệ!\nVí dụ đúng: quyenha419@gmail.com");
            return;
        }

        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;

        // 3. Gắn dữ liệu mới vào Object User
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setPassword(newPassword);

        // 4. Gọi UserDAO ra để nó tự động Update xuống Database
        try {
            UserDAO userDAO = new UserDAO();
            userDAO.update(currentUser); // Chạy hàm update trong UserDAO

            showAlert("Thành công", "Đã cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi CSDL", "Cập nhật thất bại! Tên đăng nhập hoặc Email có thể đã tồn tại.");
        }
    }

    // ========================================================
    // CÁC HÀM TIỆN ÍCH KHÁC
    // ========================================================
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

    private void addSmoothHoverEffect(Button btn) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.3), btn);
        scaleIn.setToX(1.03); scaleIn.setToY(1.03);
        ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.3), btn);
        scaleOut.setToX(1.0); scaleOut.setToY(1.0);
        btn.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.playFromStart(); });
        btn.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.playFromStart(); });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ========================================================
    // ĐIỀU HƯỚNG CHUYỂN TRANG
    // ========================================================
    @FXML public void handleLogout(ActionEvent event) { SessionManager.clear(); SceneManager.switchScene("/fxml/login.fxml", "Đăng nhập"); }
    @FXML public void handleProducts(ActionEvent event) { SceneManager.switchScene("/fxml/AdminProducts.fxml", "Products"); }
    @FXML public void handleCategories(ActionEvent event) { SceneManager.switchScene("/fxml/AdminCategories.fxml", "Categories"); }
    @FXML public void handleOrders(ActionEvent event) { SceneManager.switchScene("/fxml/AdminOrders.fxml", "Orders"); }
    @FXML public void handleCustomers(ActionEvent event) { SceneManager.switchScene("/fxml/AdminCustomers.fxml", "Customers"); }
    @FXML public void handleStock(ActionEvent event) { SceneManager.switchScene("/fxml/AdminStock.fxml", "Stock"); }
    @FXML public void handleSuppliers(ActionEvent event) { SceneManager.switchScene("/fxml/AdminSuppliers.fxml", "Suppliers"); }
    @FXML public void handleReports(ActionEvent event) { SceneManager.switchScene("/fxml/AdminReports.fxml", "Reports"); }
    @FXML public void handleUserManage(ActionEvent event) { SceneManager.switchScene("/fxml/AdminEmployees.fxml", "Employees"); }
}