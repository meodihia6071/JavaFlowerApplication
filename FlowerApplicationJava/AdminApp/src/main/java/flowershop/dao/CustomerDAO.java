package flowershop.dao;

import flowershop.models.Customer;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class CustomerDAO extends BaseDAO<Customer> {

    public CustomerDAO() {
        super(Customer.class);
    }

    // Anh dán đè hàm này vào CustomerDAO.java nhé
    public flowershop.models.Customer findByUserId(int userId) {
        try (org.hibernate.Session session = flowershop.utils.HibernateUtil.getSessionFactory().openSession()) {
            // Lưu ý: "c.user.user_id" tùy thuộc vào cách anh đặt tên biến trong class Customer
            return session.createQuery("FROM Customer c WHERE c.user.id = :userId", flowershop.models.Customer.class)
                    .setParameter("userId", userId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}