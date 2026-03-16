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
                            "left join fetch od.product " +
                            "where od.order.orderId = :orderId " +
                            "order by od.orderDetailId",
                    OrderDetail.class
            );
            query.setParameter("orderId", orderId);
            return query.list();
        }
    }

    public OrderDetail findByOrderProductAndNote(int orderId, int productId, String note) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<OrderDetail> query = session.createQuery(
                    "select od from OrderDetail od " +
                            "where od.order.orderId = :orderId " +
                            "and od.product.productId = :productId " +
                            "and coalesce(od.note, '') = :note",
                    OrderDetail.class
            );
            query.setParameter("orderId", orderId);
            query.setParameter("productId", productId);
            query.setParameter("note", note == null ? "" : note.trim());
            query.setMaxResults(1);

            List<OrderDetail> result = query.list();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    public int getTotalQuantityByOrderAndProduct(int orderId, int productId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "select coalesce(sum(od.quantity), 0) " +
                            "from OrderDetail od " +
                            "where od.order.orderId = :orderId " +
                            "and od.product.productId = :productId",
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

            OrderDetail entity = session.get(OrderDetail.class, orderDetailId);
            if (entity != null) {
                session.remove(entity);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<OrderDetail> safeList(List<OrderDetail> items) {
        return items == null ? new ArrayList<>() : items;
    }
}