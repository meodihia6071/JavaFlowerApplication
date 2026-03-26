package flowershop.dao;

import flowershop.models.Customer;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class CustomerDAO extends BaseDAO<Customer> {

    public CustomerDAO() {
        super(Customer.class);
    }

    public Customer findByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Customer> query = session.createQuery(
                    "from Customer where user.userId = :userId",
                    Customer.class
            );

            query.setParameter("userId", userId);

            return query.uniqueResult();
        }
    }
    public Customer findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Customer where lower(trim(email)) = lower(:email)",
                            Customer.class
                    ).setParameter("email", email.trim())
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
    public Customer findByPhone(String phone) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Customer where trim(phone) = :phone",
                            Customer.class
                    ).setParameter("phone", phone.trim())
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
    public Customer findByEmailOrPhone(String email, String phone) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<Customer> query = session.createQuery(
                    "from Customer where trim(email) = :email or trim(phone) = :phone",
                    Customer.class
            );

            query.setParameter("email", email.trim());
            query.setParameter("phone", phone.trim());

            return query.setMaxResults(1).uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}