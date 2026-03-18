package flowershop.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailService {

    public static void sendEmail(String name, String email, String message) {
        try {
            name = name.replace("\"", "'").trim();
            email = email.replace("\"", "'").trim();
            message = message.replace("\"", "'")
                    .replace("\n", " ")
                    .trim();

            String json = """
                    {
                      "service_id": "service_as1cg9q",
                      "template_id": "template_jsqvfke",
                      "public_key": "1e_zNSn93gQwVehkC",
                      "template_params": {
                        "from_name": "%s",
                        "from_email": "%s",
                        "message": "%s"
                      }
                    }
                    """.formatted(name, email, message);

            URL url = new URL("https://api.emailjs.com/api/v1.0/email/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // 🔥 BẮT BUỘC
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("origin", "http://localhost");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes("utf-8"));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            InputStream is = (responseCode == 200)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            if (is != null) {
                String response = new String(is.readAllBytes());
                System.out.println("RESPONSE: " + response);
            }

            if (responseCode != 200) {
                throw new RuntimeException("Send mail failed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không gửi được email");
        }
    }
}