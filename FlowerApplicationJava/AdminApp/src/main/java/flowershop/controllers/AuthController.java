package flowershop.controllers;

import flowershop.dao.CustomerDAO;
import flowershop.dao.UserDAO;
import flowershop.models.Customer;
import flowershop.models.User;
import flowershop.services.SceneManager;
import flowershop.services.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;

import java.io.IOException;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    @FXML private TextField txtLoginUsername;
    @FXML private PasswordField txtLoginPassword;
    @FXML private TextField txtLoginPasswordVisible;
    @FXML private TextField txtLastName;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtSignupUsername;
    @FXML private PasswordField txtSignupPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private TextField txtEmail;

    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (txtLoginPasswordVisible != null && txtLoginPassword != null) {
            txtLoginPasswordVisible.textProperty().bindBidirectional(txtLoginPassword.textProperty());
        }
    }

    @FXML
    private void toggleLoginPassword() {
        boolean isVisible = txtLoginPasswordVisible.isVisible();

        txtLoginPasswordVisible.setVisible(!isVisible);
        txtLoginPasswordVisible.setManaged(!isVisible);
        txtLoginPassword.setVisible(isVisible);
        txtLoginPassword.setManaged(isVisible);
    }
    @FXML
    private void handleLogin() {
        String loginInput = txtLoginUsername.getText().trim();
        String password = txtLoginPassword.getText().trim();

        if (loginInput.isEmpty() || password.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ Username/Email và Password.");
            return;
        }

        User user = null;

        if (loginInput.contains("@")) {
            Customer customerCheck = customerDAO.findByEmail(loginInput);
            if (customerCheck != null && customerCheck.getUser() != null) {
                String usernameFromEmail = customerCheck.getUser().getUsername();
                user = userDAO.findCustomerByUsernameAndPassword(usernameFromEmail, password);
            }
        }
        else {
            user = userDAO.findCustomerByUsernameAndPassword(loginInput, password);
        }

        if (user == null) {
            showAlert("Đăng nhập thất bại", "Sai tài khoản, email hoặc mật khẩu.");
            return;
        }

        Customer customer = customerDAO.findByUserId(user.getUserId());
        SessionManager.setCurrentUser(user);
        SessionManager.setCurrentCustomer(customer);

        SceneManager.switchScene("/fxml/UserHome.fxml", "User Home");
    }

    @FXML
    public void handleSignup() {
        String lastName = txtLastName.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String username = txtSignupUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtSignupPassword.getText().trim();
        String confirm = txtConfirmPassword.getText().trim();

        if (lastName.isEmpty() || firstName.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Lỗi", "Email không hợp lệ.");
            return;
        }

        if (customerDAO.findByEmail(email) != null) {
            showAlert("Lỗi", "Email đã tồn tại.");
            return;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            showAlert("Lỗi", "Mật khẩu phải >= 8 ký tự, có ít nhất 1 chữ hoa và 1 số.");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert("Lỗi", "Mật khẩu xác nhận không khớp.");
            return;
        }

        if (userDAO.findByUsername(username) != null) {
            showAlert("Lỗi", "Username đã tồn tại.");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("customer");
        userDAO.save(user);

        Customer customer = new Customer();
        customer.setCustomerName(lastName + " " + firstName);
        customer.setEmail(email);
        customer.setPoints(0);
        customer.setUser(user);
        customerDAO.save(customer);

        showAlert("Thành công", "Đăng ký thành công, hãy đăng nhập.");
        SceneManager.switchScene("/fxml/Login.fxml", "Login");
    }

    @FXML
    private void goToSignup() {
        SceneManager.switchScene("/fxml/Signup.fxml", "Sign up");
    }

    @FXML
    public void goToLogin() {
        SceneManager.switchScene("/fxml/Login.fxml", "Login");
    }

    public static void logout() {
        SessionManager.clear();
        SceneManager.switchScene("/fxml/Login.fxml", "Login");
    }

    @FXML
    private void showForgotPasswordPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ForgotPasswordPopup.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Reset Password");
            stage.setScene(new Scene(root));
            stage.setResizable(false); // Khóa resize như bạn yêu cầu
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