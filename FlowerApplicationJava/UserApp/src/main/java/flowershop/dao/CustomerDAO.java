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
                    "from Customer where user.userId = :userId", Customer.class
            );
            query.setParameter("userId", userId);
            return query.uniqueResult();
        }
    }
}