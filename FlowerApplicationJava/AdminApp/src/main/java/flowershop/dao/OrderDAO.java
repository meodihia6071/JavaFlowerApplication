package flowershop.dao;

import flowershop.models.Order;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class OrderDAO extends BaseDAO<Order> {

    public OrderDAO() {
        super(Order.class);
    }

    public Order findCartByCustomerId(int customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> query = session.createQuery(
                    "from Order where customer.customerId = :customerId and status = :status",
                    Order.class
            );
            query.setParameter("customerId", customerId);
            query.setParameter("status", "CART");
            return query.uniqueResult();
        }
    }

    public Order findByCustomerAndStatus(int customerId, String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> query = session.createQuery(
                    "from Order where customer.customerId = :customerId and status = :status",
                    Order.class
            );
            query.setParameter("customerId", customerId);
            query.setParameter("status", status);
            return query.uniqueResult();
        }
    }

    public List<Order> findAllByCustomerId(int customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> query = session.createQuery(
                    "from Order where customer.customerId = :customerId order by orderDate desc",
                    Order.class
            );
            query.setParameter("customerId", customerId);
            return query.list();
        }
    }
}