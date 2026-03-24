package flowershop.models;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private int stockId;

    // 1. NỐI VỚI BẢNG PRODUCTS (Khóa ngoại product_id)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    // 2. NỐI VỚI BẢNG SUPPLIERS (Khóa ngoại supplier_id)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplierEntity; // Đặt tên biến là supplierEntity để tránh trùng tên với hàm getSupplier() của JavaFX

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "import_price")
    private double importPrice;

    public Stock() {}

    // ==========================================
    // CÁC HÀM GETTER & SETTER CƠ BẢN
    // ==========================================

    public int getStockId() { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Supplier getSupplierEntity() { return supplierEntity; }
    public void setSupplierEntity(Supplier supplierEntity) { this.supplierEntity = supplierEntity; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getImportPrice() { return importPrice; }
    public void setImportPrice(double importPrice) { this.importPrice = importPrice; }

    public String getImportDate() { return importDate; }
    public void setImportDate(String importDate) { this.importDate = importDate; }


    // =====================================================================
    // HÀM "MẸO" CHO JAVAFX: TỰ ĐỘNG CHUYỂN ID THÀNH TÊN ĐỂ HIỂN THỊ LÊN BẢNG
    // =====================================================================

    // Hàm này khớp với: new PropertyValueFactory<>("productName") bên Controller
    public String getProductName() {
        if (this.product != null) {
            return this.product.getProductName(); // Kéo tên hoa từ bảng Product
        }
        return "Chưa xác định";
    }

    // Hàm này khớp với: new PropertyValueFactory<>("supplier") bên Controller
    public String getSupplier() {
        if (this.supplierEntity != null) {
            return this.supplierEntity.getName(); // Kéo tên nhà cung cấp từ bảng Supplier
        }
        return "Chưa xác định";
    }
}