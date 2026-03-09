package flowershop.models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    private String username;

    private String password;

    private String role;

    @OneToMany(mappedBy = "user")
    private List<Customer> customers;

    public User() {}
}