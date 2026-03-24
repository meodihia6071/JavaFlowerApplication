package flowershop.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "customer_name")
    private String customerName;

    private String phone;

    // KHÔI PHỤC LẠI TRƯỜNG EMAIL CHO KHÁCH VÃNG LAI
    private String email;

    private int points;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    public Customer() {}

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // HÀM LẤY EMAIL THÔNG MINH
    public String getEmail() {
        // Ưu tiên lấy email của tài khoản User (nếu khách này có tài khoản)
        if (this.user != null && this.user.getEmail() != null && !this.user.getEmail().isEmpty()) {
            return this.user.getEmail();
        }
        // Nếu là khách vãng lai (không có User), trả về email nhập tay
        return this.email;
    }

    // HÀM SET EMAIL MÀ CONTROLLER ĐANG BÁO THIẾU ĐÂY ANH NHÉ
    public void setEmail(String email) { this.email = email; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
}