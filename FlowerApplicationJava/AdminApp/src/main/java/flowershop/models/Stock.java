package flowershop.models;

import jakarta.persistence.*;
import java.time.LocalDate; // Thêm thư viện ngày tháng chuẩn

@Entity
@Table(name = "stock_imports")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "import_id")
    private int stockId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplierEntity;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "import_price")
    private double importPrice;

    @Column(name = "import_date")
    private LocalDate importDate; // ĐÃ FIX: Chuyển từ String sang LocalDate

    public Stock() {}

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

    public LocalDate getImportDate() { return importDate; }
    public void setImportDate(LocalDate importDate) { this.importDate = importDate; }

    public String getProductName() {
        if (this.product != null) return this.product.getProductName();
        return "Chưa xác định";
    }

    public String getSupplier() {
        if (this.supplierEntity != null) return this.supplierEntity.getName();
        return "Chưa xác định";
    }
}