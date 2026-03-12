package flowershop.dao;

import flowershop.models.User;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "from User where username = :username", User.class
            );
            query.setParameter("username", username);
            return query.uniqueResult();
        }
    }

    // Hàm cũ của nhóm anh (tôi cứ giữ lại nhỡ các bạn khác cần dùng)
    public User findCustomerByUsernameAndPassword(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "from User where username = :username and password = :password and role = :role",
                    User.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            query.setParameter("role", "customer");
            return query.uniqueResult();
        }
    }

    // Hàm login MỚI dành cho tất cả mọi người (Admin, Staff, Customer)
    public User login(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery(
                    "from User where username = :username and password = :password",
                    User.class
            );
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.uniqueResult();
        }
    }
}