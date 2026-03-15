package flowershop.dao;

import flowershop.models.OrderDetail;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class OrderDetailDAO extends BaseDAO<OrderDetail> {

    public OrderDetailDAO() {
        super(OrderDetail.class);
    }

    public OrderDetail findByOrderAndProduct(int orderId, int productId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<OrderDetail> query = session.createQuery(
                    "select od from OrderDetail od " +
                            "join fetch od.order " +
                            "join fetch od.product " +
                            "where od.order.orderId = :orderId and od.product.productId = :productId",
                    OrderDetail.class
            );
            query.setParameter("orderId", orderId);
            query.setParameter("productId", productId);
            query.setMaxResults(1);
            return query.uniqueResult();
        }
    }

    public List<OrderDetail> findByOrderId(int orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<OrderDetail> query = session.createQuery(
                    "select od from OrderDetail od " +
                            "join fetch od.product " +
                            "join fetch od.order " +
                            "where od.order.orderId = :orderId " +
                            "order by od.orderDetailId",
                    OrderDetail.class
            );
            query.setParameter("orderId", orderId);
            return query.list();
        }
    }

    public OrderDetail findByIdWithOrderAndProduct(int orderDetailId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<OrderDetail> query = session.createQuery(
                    "select od from OrderDetail od " +
                            "join fetch od.product " +
                            "join fetch od.order " +
                            "where od.orderDetailId = :orderDetailId",
                    OrderDetail.class
            );
            query.setParameter("orderDetailId", orderDetailId);
            return query.uniqueResult();
        }
    }
}