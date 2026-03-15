package flowershop.dao;

import flowershop.models.Order;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class OrderDAO extends BaseDAO<Order> {

    public OrderDAO() {
        super(Order.class);
    }

    public Order findCartByCustomerId(int customerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> query = session.createQuery(
                    "from Order where customer.customerId = :customerId and status = :status order by orderId desc",
                    Order.class
            );
            query.setParameter("customerId", customerId);
            query.setParameter("status", "CART");
            query.setMaxResults(1);
            return query.uniqueResult();
        }
    }
}