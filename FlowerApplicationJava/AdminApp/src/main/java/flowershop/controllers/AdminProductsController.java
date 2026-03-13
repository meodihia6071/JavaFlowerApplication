package flowershop.controllers;

import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class AdminProductsController {

    // =========================================================
    // XỬ LÝ CHUYỂN TRANG CHO THANH MENU DỌC (SIDEBAR)
    // =========================================================

    @FXML void goDashboard(ActionEvent event) { openSidebarFeature("/fxml/AdminDashboard.fxml", "Admin Dashboard"); }
    @FXML void goProducts(ActionEvent event) { openSidebarFeature("/fxml/AdminProducts.fxml", "Quản lý Sản phẩm"); }
    @FXML void goCategories(ActionEvent event) { openSidebarFeature("/fxml/AdminCategories.fxml", "Quản lý Danh mục"); }
    @FXML void goOrders(ActionEvent event) { openSidebarFeature("/fxml/AdminOrders.fxml", "Quản lý Đơn hàng"); }
    @FXML void goCustomers(ActionEvent event) { openSidebarFeature("/fxml/AdminCustomers.fxml", "Quản lý Khách hàng"); }
    @FXML void goSuppliers(ActionEvent event) { openSidebarFeature("/fxml/AdminSuppliers.fxml", "Quản lý Nhà cung cấp"); }
    @FXML void goStock(ActionEvent event) { openSidebarFeature("/fxml/AdminStock.fxml", "Quản lý Kho"); }
    @FXML void goReports(ActionEvent event) { openSidebarFeature("/fxml/AdminReports.fxml", "Xem Báo cáo"); }
    @FXML void goUserManage(ActionEvent event) { openSidebarFeature("/fxml/AdminUserManage.fxml", "Quản lý Tài khoản"); }

    // Hàm tiện ích chuyển trang an toàn cho Sidebar
    private void openSidebarFeature(String fxmlPath, String title) {
        try {
            // Kiểm tra xem file fxml có tồn tại không
            if (getClass().getResource(fxmlPath) == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Tính năng [" + title + "] chưa được thiết kế FXML!");
                alert.showAndWait();
            } else {
                SceneManager.switchScene(fxmlPath, title);
            }
        } catch (Exception e) {
            System.out.println("Lỗi chuyển trang: " + e.getMessage());
        }
    }

    // =========================================================
    // NÚT ĐĂNG XUẤT
    // =========================================================
    @FXML
    void handleLogout(ActionEvent event) {
        SessionManager.clear();
        try {
            SceneManager.switchScene("/fxml/login.fxml", "Đăng nhập");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}