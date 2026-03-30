package flowershop.services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private static final String FROM_EMAIL = "hakhoa1706@gmail.com";
    private static final String APP_PASSWORD = "ypld lnev ajsx qsuk";

    public static void sendEmail(String name, String userEmail, String messageContent) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(FROM_EMAIL));
        message.setSubject("🌸 Contact từ FlowerShop");

        String content = "Tên: " + name + "\nEmail: " + userEmail + "\n\nNội dung:\n" + messageContent;
        message.setText(content);
        Transport.send(message);

        Message reply = new MimeMessage(session);
        reply.setFrom(new InternetAddress(FROM_EMAIL));
        reply.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
        reply.setSubject("🌸 FlowerShop - Cảm ơn bạn đã liên hệ");

        reply.setContent(
                "<h2>🌸 FlowerShop</h2>" +
                        "<p>Xin chào <b>" + name + "</b>,</p>" +
                        "<p>Cảm ơn bạn đã liên hệ với chúng tôi 💌</p>" +
                        "<p>Chúng tôi sẽ phản hồi sớm nhất!</p>",
                "text/html; charset=UTF-8"
        );
        Transport.send(reply);
    }

    public static void sendOTPEmail(String recipientEmail, String otpCode) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL, "FlowerShop Security"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("🔑 Mã xác thực đổi mật khẩu - FlowerShop");

        String htmlContent =
                "<div style='font-family: Arial, sans-serif; border: 1px solid #ddd; padding: 20px; border-radius: 10px; max-width: 500px;'>" +
                        "<h2 style='color: #ff5c9c; text-align: center;'>🌸 FlowerShop Security</h2>" +
                        "<p>Xin chào,</p>" +
                        "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Mã xác thực (OTP) của bạn là:</p>" +
                        "<div style='text-align: center; margin: 20px 0;'>" +
                        "<span style='font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #333; background: #f4f4f4; padding: 10px 20px; border-radius: 5px;'>" + otpCode + "</span>" +
                        "</div>" +
                        "<p style='color: #777; font-size: 13px;'>Mã này có hiệu lực trong vòng <b>2 phút</b>. Nếu bạn không yêu cầu đổi mật khẩu, vui lòng bỏ qua email này để đảm bảo an toàn.</p>" +
                        "<hr style='border: none; border-top: 1px solid #eee;'>" +
                        "<p style='font-size: 11px; color: #aaa; text-align: center;'>Đây là email tự động, vui lòng không phản hồi.</p>" +
                        "</div>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");
        Transport.send(message);
    }
}