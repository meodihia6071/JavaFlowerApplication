package flowershop.dao;

import flowershop.models.OrderDetail;
import flowershop.utils.HibernateUtil;

import org.hibernate.Session;

import java.util.List;

public class OrderDetailDAO {

    public List<OrderDetail> getByOrderId(int orderId){

        Session session = HibernateUtil.getSessionFactory().openSession();

        List<OrderDetail> list = session.createQuery(
                        "SELECT od FROM OrderDetail od " +
                                "JOIN FETCH od.product " +
                                "WHERE od.order.orderId = :id",
                        OrderDetail.class
                ).setParameter("id", orderId)
                .list();

        session.close();
        return list;
    }
}