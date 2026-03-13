package flowershop.controllers;

import flowershop.models.User;
import flowershop.models.Customer;
import flowershop.dao.CustomerDAO;
import flowershop.services.SceneManager;     // vẫn giữ vì handleLogout dùng nó
import flowershop.services.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.hibernate.Session;
import flowershop.utils.HibernateUtil;
public class AdminDashboardController {

    @FXML private ImageView avatarImageView;
    @FXML private TextField txtEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtRole;

    // Khai báo 2 ô mật khẩu và 1 nút con mắt
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button btnTogglePassword;

    @FXML private Button btnUpdate;
    @FXML private Button btnLogout;

    // Biến trạng thái: false = đang ẩn (••••), true = đang hiện (123456)
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            // Đổ mật khẩu vào cả 2 ô để luôn đồng bộ
            txtPassword.setText(currentUser.getPassword());
            txtPasswordVisible.setText(currentUser.getPassword());

            // Ban đầu ẩn ô TextField, hiện ô PasswordField
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);

            // Hiển thị Role
            if ("admin".equalsIgnoreCase(currentUser.getRole())) {
                txtRole.setText("Quản trị viên (Admin)");
            } else {
                txtRole.setText("Nhân viên (Staff)");
            }

            // Lấy thông tin Tên/Email từ bảng Customer
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customerData = customerDAO.findByUserId(currentUser.getUserId());

            if (customerData != null) {
                txtName.setText(customerData.getCustomerName());
                txtEmail.setText(customerData.getEmail() != null ? customerData.getEmail() : "");
            } else {
                txtName.setText(currentUser.getUsername());
                txtEmail.setText("");
            }
        }
    }

    // =========================================================
    // HÀM XỬ LÝ ẨN/HIỆN MẬT KHẨU
    // =========================================================
    @FXML
    void togglePasswordVisibility(ActionEvent event) {
        if (!isPasswordVisible) {
            // Chuyển sang CHẾ ĐỘ HIỆN
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);

            txtPassword.setVisible(false);
            txtPassword.setManaged(false);

            btnTogglePassword.setText("Ẩn");
            isPasswordVisible = true;
        } else {
            // Chuyển sang CHẾ ĐỘ ẨN
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);

            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);

            btnTogglePassword.setText("Hiện");
            isPasswordVisible = false;
        }
    }

    @FXML
    void handleUpdateProfile(ActionEvent event) {
        String newName = txtName.getText().trim();
        String newEmail = txtEmail.getText().trim();

        // Lấy pass từ ô đang hiển thị
        String newPassword = isPasswordVisible ? txtPasswordVisible.getText().trim() : txtPassword.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            showErrorAlert("Lỗi nhập liệu", "Vui lòng điền đầy đủ Tên, Email và Mật khẩu!");
            return;
        }

        // ... (Giữ nguyên phần bắt lỗi Regex Email và kiểm tra trùng lặp DB như bạn đã gửi trước) ...

        User currentUser = SessionManager.getCurrentUser();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Kiểm tra trùng username/email
            Long userCount = session.createQuery("select count(u) from User u where u.username = :username and u.userId <> :userId", Long.class)
                    .setParameter("username", newName)
                    .setParameter("userId", currentUser.getUserId())
                    .uniqueResult();
            if (userCount > 0) {
                showErrorAlert("Trùng", "Tên đăng nhập đã tồn tại!");
                return;
            }

            session.beginTransaction();
            currentUser.setUsername(newName);
            currentUser.setPassword(newPassword);
            session.merge(currentUser);

            CustomerDAO customerDAO = new CustomerDAO();
            Customer customerData = customerDAO.findByUserId(currentUser.getUserId());
            if (customerData != null) {
                customerData.setCustomerName(newName);
                customerData.setEmail(newEmail);
                session.merge(customerData);
            } else {
                Customer newC = new Customer();
                newC.setCustomerName(newName);
                newC.setEmail(newEmail);
                newC.setUser(currentUser);
                session.persist(newC);
            }
            session.getTransaction().commit();

            // Cập nhật lại Text cho cả 2 ô sau khi lưu
            txtPassword.setText(newPassword);
            txtPasswordVisible.setText(newPassword);
            SessionManager.setCurrentUser(currentUser);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Cập nhật thành công!");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        SessionManager.clear();
        try {
            SceneManager.switchScene("/fxml/login.fxml", "Đăng nhập");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ====================== MỞ CỬA SỔ MỚI (NEW WINDOW) ======================
    // Đây là đúng ý bạn: giống như Login → AdminDashboard, khi nhấn Products/Categories... sẽ mở cửa sổ mới hoàn toàn

    private void openNewWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(scene);
            newStage.setResizable(true);
            newStage.setMinWidth(1100);   // kích thước phù hợp với layout bạn muốn
            newStage.setMinHeight(700);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Lỗi mở cửa sổ", "Không thể mở: " + title + "\nKiểm tra đường dẫn FXML hoặc file tồn tại chưa!");
        }
    }

    @FXML void handleProducts(ActionEvent event) { openFeature("/fxml/AdminProducts.fxml", "Quản lý Sản phẩm"); }
    @FXML void handleCategories(ActionEvent event) { openFeature("/fxml/AdminCategories.fxml", "Quản lý Danh mục"); }
    @FXML void handleOrders(ActionEvent event) { openFeature("/fxml/AdminOrders.fxml", "Quản lý Đơn hàng"); }
    @FXML void handleCustomers(ActionEvent event) { openFeature("/fxml/AdminCustomers.fxml", "Quản lý Khách hàng"); }
    @FXML void handleStock(ActionEvent event) { openFeature("/fxml/AdminStock.fxml", "Quản lý Kho"); }
    @FXML void handleSuppliers(ActionEvent event) { openFeature("/fxml/AdminSuppliers.fxml", "Quản lý Nhà cung cấp"); }
    @FXML void handleReports(ActionEvent event) { openFeature("/fxml/AdminReports.fxml", "Xem Báo cáo"); }
    @FXML void handleUserManage(ActionEvent event) { openFeature("/fxml/AdminUserManage.fxml", "Quản lý Tài khoản"); }

    // Hàm chuyển trang thông minh: Có file thì mở, chưa có file thì báo Coming Soon
    private void openFeature(String fxmlPath, String title) {
        try {
            // Kiểm tra xem file FXML đã được anh Quyền tạo chưa
            if (getClass().getResource(fxmlPath) == null) {
                showComingSoonAlert(title);
            } else {
                SceneManager.switchScene(fxmlPath, title);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showComingSoonAlert(title);
        }
    }

    private void showComingSoonAlert(String featureName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo hệ thống");
        alert.setHeaderText(null);
        alert.setContentText("Giao diện [" + featureName + "] đang được thiết kế. Vui lòng quay lại sau!");
        alert.showAndWait();
    }}