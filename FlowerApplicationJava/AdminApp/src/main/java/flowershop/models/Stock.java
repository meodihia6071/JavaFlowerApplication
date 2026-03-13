package flowershop.models;

import jakarta.persistence.*;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "last_update")
    private String lastUpdate;

    public Stock(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
