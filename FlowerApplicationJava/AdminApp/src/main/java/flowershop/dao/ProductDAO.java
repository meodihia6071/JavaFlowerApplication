package flowershop.dao;

import flowershop.models.Product;

public class ProductDAO extends BaseDAO<Product> {

    public ProductDAO() {
        super(Product.class);
    }
}