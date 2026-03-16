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
                    "select distinct o from Order o " +
                            "left join fetch o.orderDetails od " +
                            "left join fetch od.product " +
                            "where o.customer.customerId = :customerId and o.status = :status " +
                            "order by o.orderId desc",
                    Order.class
            );
            query.setParameter("customerId", customerId);
            query.setParameter("status", "CART");
            query.setMaxResults(1);

            List<Order> orders = query.list();
            return orders.isEmpty() ? null : orders.get(0);
        }
    }
}