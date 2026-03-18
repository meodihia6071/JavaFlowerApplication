package flowershop.dao;

import flowershop.models.Order;
import flowershop.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class OrderDAO {

    // ===== GET ALL =====
    public List<Order> getAllOrders(){

        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Order> list = session.createQuery(
                "SELECT DISTINCT o FROM Order o " +
                        "LEFT JOIN FETCH o.customer " +
                        "ORDER BY o.orderDate DESC",
                Order.class
        ).list();

        session.close();
        return list;
    }

    // ===== SAVE =====
    public void save(Order order){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.save(order);

        tx.commit();
        session.close();
    }

    // ===== UPDATE =====
    public void update(Order order){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.update(order);

        tx.commit();
        session.close();
    }

    // ===== DELETE =====
    public void delete(Order order){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.delete(order);

        tx.commit();
        session.close();
    }
}