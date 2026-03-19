package flowershop.dao;

import flowershop.models.Customer;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CustomerDAO {

    public Customer findByUserId(int userId){
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Customer c WHERE c.user.userId = :uid",
                            Customer.class
                    )
                    .setParameter("uid", userId)
                    .uniqueResult();
        }
    }

    public List<Customer> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Customer", Customer.class).list();
        }
    }

    public void save(Customer customer) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(customer);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void update(Customer customer) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(customer);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }

    public void delete(Customer customer) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(customer);
            tx.commit();
        }
    }
}