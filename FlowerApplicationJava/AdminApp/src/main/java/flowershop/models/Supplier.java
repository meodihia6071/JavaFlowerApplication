package flowershop.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private int supplierId;

    @Column(name = "name", nullable = false)
    private String supplierName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "status")
    private String status;

    // Constructor rỗng (bắt buộc cho JPA)
    public Supplier() {
    }

    // Constructor đầy đủ
    public Supplier(int supplierId, String supplierName, String phone, String email,
                    String address, LocalDate createdDate, String status) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.createdDate = createdDate;
        this.status = status;
    }

    // Constructor khi thêm mới (không cần id)
    public Supplier(String supplierName, String phone, String email,
                    String address, LocalDate createdDate, String status) {
        this.supplierName = supplierName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.createdDate = createdDate;
        this.status = status;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return supplierName;
    }
}
