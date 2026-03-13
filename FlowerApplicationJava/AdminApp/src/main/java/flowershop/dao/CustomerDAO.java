package flowershop.dao;

import flowershop.models.Customer;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class CustomerDAO extends BaseDAO<Customer> {

    public CustomerDAO() {
        super(Customer.class);
    }

    // Thêm hàm này để tìm Profile dựa theo ID của User đang đăng nhập
    public Customer findByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Lưu ý: Chữ "user.userId" hay "userId" phụ thuộc vào cách anh map trong file Customer.java
            // Dưới đây là cách map phổ biến nhất nếu anh dùng @ManyToOne hoặc @OneToOne
            Query<Customer> query = session.createQuery(
                    "from Customer c where c.user.userId = :userId", Customer.class
            );
            query.setParameter("userId", userId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}