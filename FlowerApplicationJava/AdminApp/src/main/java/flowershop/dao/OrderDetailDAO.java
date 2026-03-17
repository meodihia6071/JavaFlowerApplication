package flowershop.dao;

import flowershop.models.OrderDetail;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO extends BaseDAO<OrderDetail> {

    public OrderDetailDAO() {
        super(OrderDetail.class);
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
            return safeList(query.list());
        }
    }

    public OrderDetail findByIdWithProduct(int orderDetailId) {
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

    public OrderDetail findByOrderAndProductAndNote(int orderId, int productId, String note) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<OrderDetail> query = session.createQuery(
                    "from OrderDetail " +
                            "where order.orderId = :orderId " +
                            "and product.productId = :productId " +
                            "and ((:note is null and note is null) or note = :note)",
                    OrderDetail.class
            );
            query.setParameter("orderId", orderId);
            query.setParameter("productId", productId);
            query.setParameter("note", note);
            return query.uniqueResult();
        }
    }

    public int getTotalQuantityByOrderAndProduct(int orderId, int productId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "select coalesce(sum(quantity), 0) from OrderDetail " +
                            "where order.orderId = :orderId and product.productId = :productId",
                    Long.class
            );
            query.setParameter("orderId", orderId);
            query.setParameter("productId", productId);

            Long result = query.uniqueResult();
            return result == null ? 0 : result.intValue();
        }
    }

    public void deleteById(int orderDetailId) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            OrderDetail detail = session.get(OrderDetail.class, orderDetailId);
            if (detail != null) {
                session.remove(detail);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    private List<OrderDetail> safeList(List<OrderDetail> items) {
        return items == null ? new ArrayList<>() : items;
    }
}