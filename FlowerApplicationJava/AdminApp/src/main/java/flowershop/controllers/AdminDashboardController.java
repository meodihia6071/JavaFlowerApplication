package flowershop.controllers;

import flowershop.models.User;
import flowershop.models.Customer;
import flowershop.dao.CustomerDAO;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

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
                // Sửa thành getUserId() cho khớp với DB chuẩn
                Customer profile = customerDAO.findByUserId(currentUser.getUserId());

                if (profile != null) {
                    // Nếu tìm thấy -> Đổ dữ liệu thật lên màn hình!
                    txtName.setText(profile.getCustomerName());
                    if (profile.getEmail() != null) {
                        txtEmail.setText(profile.getEmail());
                    } else {
                        txtEmail.setText("");
                    }
                } else {
                    // Đề phòng trường hợp Admin mới tạo bên users mà chưa kịp tạo profile bên customers
                    txtName.setText(currentUser.getUsername());
                    txtEmail.setText("Chưa liên kết hồ sơ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Lỗi khi kéo dữ liệu từ bảng customers sang!");
            }
        }
    }

    // HÀM TẠO HIỆU ỨNG PHÓNG TO NÚT 0.3 GIÂY
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

    // Đã thêm tính năng Check Email bằng Regex vào đây
    @FXML
    public void handleUpdateProfile(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (email == null || email.trim().isEmpty()) {
            showAlert("Lỗi", "Email không được để trống!");
            return;
        }

        // Kiểm tra định dạng Email
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

    // =========================================================
    // NÚT ĐĂNG XUẤT (BAY THẲNG VỀ TRANG LOGIN VỚI FADE-IN)
    // =========================================================
    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Xóa phiên đăng nhập hiện tại
            SessionManager.clear();

            // Tải trang Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Đăng nhập");

            // Ép tàng hình và đổi Root
            root.setOpacity(0);
            stage.getScene().setRoot(root);

            // Chạy hiệu ứng Fade-in 0.4s
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải trang Đăng nhập!");
        }
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
    // HÀM CHUYỂN TRANG CHỨC NĂNG (ĐÃ TÍCH HỢP FADE-IN 0.4s)
    // =========================================================
    private void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);

            // Ép tàng hình và đổi Root
            root.setOpacity(0);
            stage.getScene().setRoot(root);

            // Chạy hiệu ứng Fade-in
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải file FXML: " + fxmlPath);
        }
    }

    @FXML
    public void handleCategories(ActionEvent event) {
        switchScene(event, "/fxml/AdminCategories.fxml", "Quản Lý Danh Mục");
    }

    @FXML
    public void handleProducts(ActionEvent event) {
        switchScene(event, "/fxml/AdminProducts.fxml", "Quản Lý Sản Phẩm");
    }

    @FXML
    public void handleOrders(ActionEvent event) {
        switchScene(event, "/fxml/AdminOrders.fxml", "Quản Lý Đơn Hàng");
    }

    @FXML
    public void handleCustomers(ActionEvent event) {
        SceneManager.switchScene("/fxml/AdminCustomers.fxml", "Customers");
    }

    @FXML
    public void handleStock(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminStock.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Stock Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSuppliers(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminSuppliers.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Suppliers Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleReports(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminReports.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reports");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUserManage(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/AdminEmployees.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Employee Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}