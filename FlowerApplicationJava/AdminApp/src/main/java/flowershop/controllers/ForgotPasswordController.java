package flowershop.controllers;

import flowershop.dao.UserDAO;
import flowershop.dao.CustomerDAO;
import flowershop.models.User;
import flowershop.services.EmailService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class ForgotPasswordController implements Initializable {
    @FXML private TextField txtResetEmail, txtOTP;
    @FXML private PasswordField txtNewPass, txtConfirmNewPass;
    @FXML private TextField txtNewPassVisible;
    @FXML private TextField txtConfirmNewPassVisible;
    @FXML private VBox stepEmail, stepOTP, stepNewPass;
    @FXML private Label lblMessage, lblTimer;
    @FXML private Button btnResend;

    private String generatedOTP;
    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private Timeline timeline;
    private int timeSeconds;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (txtNewPassVisible != null && txtNewPass != null) {
            txtNewPassVisible.textProperty().bindBidirectional(txtNewPass.textProperty());
        }
        if (txtConfirmNewPassVisible != null && txtConfirmNewPass != null) {
            txtConfirmNewPassVisible.textProperty().bindBidirectional(txtConfirmNewPass.textProperty());
        }
    }

    @FXML
    private void toggleNewPassword() {
        boolean isVisible = txtNewPassVisible.isVisible();
        txtNewPassVisible.setVisible(!isVisible);
        txtNewPassVisible.setManaged(!isVisible);
        txtNewPass.setVisible(isVisible);
        txtNewPass.setManaged(isVisible);
    }

    @FXML
    private void toggleConfirmPassword() {
        boolean isVisible = txtConfirmNewPassVisible.isVisible();
        txtConfirmNewPassVisible.setVisible(!isVisible);
        txtConfirmNewPassVisible.setManaged(!isVisible);
        txtConfirmNewPass.setVisible(isVisible);
        txtConfirmNewPass.setManaged(isVisible);
    }
    @FXML
    private void handleSendOTP() {
        String email = txtResetEmail.getText().trim();
        if (email.isEmpty()) {
            setMessage("Vui lòng nhập Email!", false);
            return;
        }
        if (customerDAO.findByEmail(email) == null) {
            setMessage("Email không tồn tại trong hệ thống!", false);
            return;
        }

        lblMessage.setText("Đang gửi mail, vui lòng chờ...");
        lblMessage.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14px;");

        new Thread(() -> {
            try {
                generatedOTP = String.valueOf((int)(Math.random() * 900000) + 100000);
                EmailService.sendOTPEmail(email, generatedOTP);

                Platform.runLater(() -> {
                    switchStep(stepEmail, stepOTP);
                    setMessage("Mã OTP đã được gửi đến Email của bạn!", true);
                    startTimer(120);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setMessage("Lỗi gửi mail. Vui lòng kiểm tra kết nối.", false);
                });
            }
        }).start();
    }

    @FXML
    private void handleResendOTP() {
        txtOTP.clear();
        handleSendOTP();
    }

    @FXML
    private void handleVerifyOTP() {
        String inputOTP = txtOTP.getText().trim();
        if (generatedOTP == null) {
            setMessage("Mã OTP đã hết hạn. Vui lòng gửi lại!", false);
            return;
        }
        if (!inputOTP.equals(generatedOTP)) {
            setMessage("Mã OTP không chính xác!", false);
            return;
        }

        if (timeline != null) timeline.stop();
        switchStep(stepOTP, stepNewPass);
        setMessage("Xác thực thành công! Vui lòng đặt mật khẩu mới.", true);
    }

    @FXML
    private void handleResetPassword() {
        String pass = txtNewPass.getText();
        String confirm = txtConfirmNewPass.getText();

        if (pass.isEmpty() || confirm.isEmpty()) {
            setMessage("Vui lòng nhập đầy đủ mật khẩu!", false);
            return;
        }
        if (!pass.equals(confirm)) {
            setMessage("Mật khẩu xác nhận không khớp!", false);
            return;
        }
        if (!pass.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            setMessage("Mật khẩu phải >= 8 ký tự, có 1 chữ hoa và 1 số!", false);
            return;
        }

        try {
            User user = customerDAO.findByEmail(txtResetEmail.getText()).getUser();
            user.setPassword(pass);
            userDAO.update(user);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Đổi mật khẩu thành công! Bạn có thể đăng nhập ngay.");
            alert.setHeaderText(null);
            alert.showAndWait();

            txtNewPass.getScene().getWindow().hide();
        } catch (Exception e) {
            setMessage("Cập nhật thất bại!", false);
        }
    }

    private void startTimer(int seconds) {
        if (timeline != null) timeline.stop();
        timeSeconds = seconds;

        btnResend.setDisable(true);
        btnResend.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds--;
            int min = timeSeconds / 60;
            int sec = timeSeconds % 60;
            lblTimer.setText(String.format("Hết hạn sau: %02d:%02d", min, sec));

            if (timeSeconds <= 0) {
                timeline.stop();
                lblTimer.setText("Mã OTP đã hết hạn!");
                generatedOTP = null;
                btnResend.setDisable(false);
                btnResend.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void switchStep(VBox currentStep, VBox nextStep) {
        currentStep.setVisible(false); currentStep.setManaged(false);
        nextStep.setVisible(true); nextStep.setManaged(true);
    }

    private void setMessage(String msg, boolean isSuccess) {
        lblMessage.setText(msg);
        lblMessage.setStyle(isSuccess ? "-fx-text-fill: #27ae60; -fx-font-size: 14px;" : "-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
    }
}