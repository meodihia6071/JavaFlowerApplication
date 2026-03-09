package flowershop.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "order_date")
    private LocalDate orderDate;

    private BigDecimal total;

    @Column(name = "points_used")
    private int pointsUsed;

    @Column(name = "points_earned")
    private int pointsEarned;

    private String status;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    public Order() {}
}