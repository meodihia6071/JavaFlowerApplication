package flowershop.dao;

import flowershop.models.Product;
import flowershop.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ProductDAO {

    // ===== GET ALL =====
    public List<Product> getAllProducts(){

        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Product> list = session.createQuery(
                "SELECT p FROM Product p JOIN FETCH p.category",
                Product.class
        ).list();

        session.close();

        return list;
    }

    // ===== SAVE =====
    public void save(Product product){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.save(product);

        tx.commit();
        session.close();
    }

    // ===== UPDATE =====
    public void update(Product product){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.update(product);

        tx.commit();
        session.close();
    }

    // ===== DELETE =====
    public void delete(Product product){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.delete(product);

        tx.commit();
        session.close();
    }
}