package flowershop.controllers;

import flowershop.dao.CustomerDAO;
import flowershop.dao.OrderDAO;
import flowershop.dao.UserDAO;
import flowershop.models.Customer;
import flowershop.models.Order;
import flowershop.models.User;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProfileController {

    @FXML private TextField txtUsername, txtFullName, txtEmail, txtPhone;


    @FXML private PasswordField txtNewPassword, txtConfirmPassword;
    @FXML private TextField txtNewPasswordVisible, txtConfirmPasswordVisible;

    @FXML private Label lblDisplayName;
    @FXML private VBox paneHistory;
    @FXML private TableView<Order> tableHistory;
    @FXML private TableColumn<Order, Integer> colId, colPointsUsed, colPointsEarned;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, Object> colDate;
    @FXML private TableColumn<Order, Object> colTotal;

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private Customer currentCustomer;

    @FXML
    public void initialize() {
        currentCustomer = SessionManager.getCurrentCustomer();

        colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        setupCenterColumn(colId);

        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colDate.setCellFactory(column -> new TableCell<Order, Object>() { // Dùng Object để nhận mọi kiểu dữ liệu
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Kiểm tra nếu là LocalDateTime (kiểu chuẩn từ Hibernate)
                    if (item instanceof LocalDateTime) {
                        LocalDateTime dateTime = (LocalDateTime) item;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        setText(dateTime.format(formatter));
                    } else {
                        // Nếu lỡ là String hoặc kiểu khác thì dùng toString() và xóa chữ T
                        setText(item.toString().replace("T", " ").substring(0, 16));
                    }
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        // 3. Cột Dùng Point - Căn giữa & Quy đổi ra tiền (x100)
        colPointsUsed.setCellValueFactory(new PropertyValueFactory<>("pointsUsed"));
        colPointsUsed.setCellFactory(column -> new TableCell<Order, Integer>() {
            @Override
            protected void updateItem(Integer points, boolean empty) {
                super.updateItem(points, empty);
                if (empty || points == null) {
                    setText(null);
                } else {
                    setText(String.format("-%,d đ", points * 1000));
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });

        // 4. Cột Tổng Tiền - Fix lỗi Cast & Căn giữa
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(column -> new TableCell<Order, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    double value = 0;
                    if (item instanceof BigDecimal) {
                        value = ((BigDecimal) item).doubleValue();
                    } else if (item instanceof Double) {
                        value = (Double) item;
                    }
                    setText(String.format("%,.0f đ", value));
                    setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
                }
            }
        });

        // 5. Cột Nhận Point - Căn giữa
        colPointsEarned.setCellValueFactory(new PropertyValueFactory<>("pointsEarned"));
        colPointsEarned.setCellFactory(column -> new TableCell<Order, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText("+" + item);
                    setStyle("-fx-alignment: CENTER; -fx-text-fill: #27ae60;");
                }
            }
        });

        // 6. Cột Trạng Thái - Căn giữa
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        setupCenterColumn(colStatus);

        setupColumnWidths();

        if (currentCustomer != null) fillProfileData();
    }

    private <T> void setupCenterColumn(TableColumn<Order, T> column) {
        column.setCellFactory(col -> new TableCell<Order, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    setText(item.toString());
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    private void setupColumnWidths() {
        colId.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        colDate.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        colPointsUsed.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        colTotal.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        colPointsEarned.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        colStatus.setMaxWidth(1f * Integer.MAX_VALUE * 15);
    }

    private void fillProfileData() {
        txtUsername.setText(currentCustomer.getUser().getUsername());
        txtFullName.setText(currentCustomer.getCustomerName());
        txtEmail.setText(currentCustomer.getEmail());
        txtPhone.setText(currentCustomer.getPhone() != null ? currentCustomer.getPhone() : "");
        lblDisplayName.setText(currentCustomer.getCustomerName());
    }

    @FXML
    private void toggleNewPassword() {
        toggleField(txtNewPassword, txtNewPasswordVisible);
    }

    @FXML
    private void toggleConfirmPassword() {
        toggleField(txtConfirmPassword, txtConfirmPasswordVisible);
    }

    private void toggleField(PasswordField pf, TextField tf) {
        if (pf.isVisible()) {
            tf.setText(pf.getText());
            tf.setVisible(true);
            pf.setVisible(false);
        } else {
            pf.setText(tf.getText());
            pf.setVisible(true);
            tf.setVisible(false);
        }
    }

    @FXML
    private void handleUpdateInfo() {
        String name = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            showAlert("Lỗi", "Họ tên và Email không được để trống!", Alert.AlertType.ERROR);
            return;
        }

        try {
            currentCustomer.setCustomerName(name);
            currentCustomer.setEmail(email);
            currentCustomer.setPhone(phone);
            customerDAO.update(currentCustomer);
            SessionManager.setCurrentCustomer(currentCustomer);
            lblDisplayName.setText(name);
            showAlert("Thành công", "Đã cập nhật thông tin!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Lỗi", "Lỗi lưu dữ liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleChangePassword() {
        String pass = txtNewPassword.isVisible() ? txtNewPassword.getText() : txtNewPasswordVisible.getText();
        String confirm = txtConfirmPassword.isVisible() ? txtConfirmPassword.getText() : txtConfirmPasswordVisible.getText();

        if (pass.isEmpty() || !pass.equals(confirm)) {
            showAlert("Lỗi", "Mật khẩu không khớp hoặc trống!", Alert.AlertType.ERROR);
            return;
        }

        if (!pass.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            showAlert("Lỗi", "Mật khẩu cần ít nhất 8 ký tự, 1 chữ hoa, 1 số!", Alert.AlertType.ERROR);
            return;
        }

        try {
            User user = currentCustomer.getUser();
            user.setPassword(pass);
            userDAO.update(user);
            txtNewPassword.clear(); txtNewPasswordVisible.clear();
            txtConfirmPassword.clear(); txtConfirmPasswordVisible.clear();
            showAlert("Thành công", "Đổi mật khẩu thành công!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Lỗi", "Lỗi: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void showDeliveryHistory() {
        paneHistory.setManaged(true);
        paneHistory.setVisible(true);
        List<Order> orders = orderDAO.findAllByCustomerId(currentCustomer.getCustomerId());
        tableHistory.setItems(FXCollections.observableArrayList(orders));
    }

    @FXML private void hideHistory() { paneHistory.setManaged(false); paneHistory.setVisible(false); }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML public void goHome() { SceneManager.switchScene("/fxml/UserHome.fxml", "Home"); }
    @FXML public void goFlowers() { SceneManager.switchScene("/fxml/Flowers.fxml", "Flowers"); }
    @FXML public void goCategories() { SceneManager.switchScene("/fxml/Categories.fxml", "Categories"); }
    @FXML public void goContact() { SceneManager.switchScene("/fxml/Contact.fxml", "Contact"); }
    @FXML public void goCart() { SceneManager.switchScene("/fxml/Cart.fxml", "Cart"); }
    @FXML public void goProfile() { SceneManager.switchScene("/fxml/Profile.fxml", "Profile"); }
    @FXML public void handleLogout() { SessionManager.clear(); SceneManager.switchScene("/fxml/Login.fxml", "Login"); }
}