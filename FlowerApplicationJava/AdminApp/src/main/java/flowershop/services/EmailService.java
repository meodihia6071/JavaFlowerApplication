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

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                    }
                });

        // 🔥 1. Gửi mail cho shop
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(FROM_EMAIL));

        message.setSubject("🌸 Contact từ FlowerShop");

        String content =
                "Tên: " + name +
                        "\nEmail: " + userEmail +
                        "\n\nNội dung:\n" + messageContent;

        message.setText(content);

        Transport.send(message);

        // 🔥 2. Gửi mail phản hồi cho khách
        Message reply = new MimeMessage(session);
        reply.setFrom(new InternetAddress(FROM_EMAIL));
        reply.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(userEmail));

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
}