package flowershop.dao;

import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class BaseDAO<T> {

    private Class<T> type;

    public BaseDAO(Class<T> type) {
        this.type = type;
    }

    public void save(T entity) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.persist(entity);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) transaction.rollback();

            e.printStackTrace();
        }
    }

    public void update(T entity) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.merge(entity);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) transaction.rollback();

            e.printStackTrace();
        }
    }

    public void delete(T entity) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.remove(entity);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) transaction.rollback();

            e.printStackTrace();
        }
    }

    public List<T> findAll() {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.createQuery(
                    "from " + type.getSimpleName(), type
            ).list();
        }
    }

    public T findById(int id) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            return session.get(type, id);
        }
    }
}