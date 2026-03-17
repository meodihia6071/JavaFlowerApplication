package flowershop.controllers;

import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminCategoriesController {

    // =========================================================
    // XỬ LÝ CHUYỂN TRANG CHO THANH MENU DỌC (SIDEBAR)
    // =========================================================

    // Đã thêm việc truyền 'event' vào để lấy thông tin cửa sổ hiện tại
    @FXML void goDashboard(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminDashboard.fxml", "Admin Dashboard"); }
    @FXML void goProducts(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminProducts.fxml", "Quản lý Sản phẩm"); }
    @FXML void goCategories(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminCategories.fxml", "Quản lý Danh mục"); }
    @FXML void goOrders(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminOrders.fxml", "Quản lý Đơn hàng"); }
    @FXML void goCustomers(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminCustomers.fxml", "Quản lý Khách hàng"); }
    @FXML void goSuppliers(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminSuppliers.fxml", "Quản lý Nhà cung cấp"); }
    @FXML void goStock(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminStock.fxml", "Quản lý Kho"); }
    @FXML void goReports(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminReports.fxml", "Xem Báo cáo"); }
    @FXML void goUserManage(ActionEvent event) { openSidebarFeature(event, "/fxml/AdminUserManage.fxml", "Quản lý Tài khoản"); }

    // Hàm tiện ích chuyển trang có tích hợp HIỆU ỨNG FADE-IN 0.4s
    private void openSidebarFeature(ActionEvent event, String fxmlPath, String title) {
        try {
            // Kiểm tra xem file fxml có tồn tại không
            if (getClass().getResource(fxmlPath) == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Tính năng [" + title + "] chưa được thiết kế FXML!");
                alert.showAndWait();
            } else {
                // Tải giao diện mới
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                // Lấy cửa sổ (Stage) hiện tại
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle(title);

                // Ép giao diện tàng hình (Opacity = 0) và thay đổi Root
                root.setOpacity(0);
                stage.getScene().setRoot(root);

                // Chạy hiệu ứng hiện dần lên cực mượt
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi chuyển trang: " + e.getMessage());
        }
    }

    // =========================================================
    // NÚT ĐĂNG XUẤT (CŨNG ĐƯỢC THÊM HIỆU ỨNG LUÔN)
    // =========================================================
    @FXML
    void handleLogout(ActionEvent event) {
        SessionManager.clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Đăng nhập");

            root.setOpacity(0);
            stage.getScene().setRoot(root);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}