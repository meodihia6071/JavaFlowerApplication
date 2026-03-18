package flowershop.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PaymentService {

    public static String createQRUrl(long amount, int orderId) {

        String bank = "MB"; // bạn có thể đổi
        String account = "0919098606"; // số tk demo

        return "https://img.vietqr.io/image/"
                + bank + "-" + account + "-compact2.png"
                + "?amount=" + amount*25000
                + "&addInfo=ThanhToanDonHang" + orderId;
    }
}