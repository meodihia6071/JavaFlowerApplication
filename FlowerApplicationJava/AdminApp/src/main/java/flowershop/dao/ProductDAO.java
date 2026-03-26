package flowershop.dao;

import flowershop.models.Product;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ProductDAO extends BaseDAO<Product> {

    public ProductDAO() {
        super(Product.class);
    }

    public Product findByProductName(String productName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Product> query = session.createQuery(
                    "from Product where productName = :productName",
                    Product.class
            );
            query.setParameter("productName", productName);
            return query.uniqueResult();
        }
    }
    public List<Product> findRandomInStock(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Product WHERE quantity > 0 ORDER BY rand()",
                            Product.class
                    ).setMaxResults(limit)
                    .list();
        }
    }
    public List<Product> findTop5ByCategoryId(int categoryId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Product p WHERE p.category.categoryId = :id ORDER BY rand()",
                            Product.class
                    )
                    .setParameter("id", categoryId)
                    .setMaxResults(5)
                    .list();
        }
    }
    public List<Product> findAllWithCategory() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select distinct p from Product p left join fetch p.category",
                    Product.class
            ).list();
        }
    }
}