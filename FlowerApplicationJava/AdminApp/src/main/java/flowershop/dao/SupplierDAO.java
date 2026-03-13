package flowershop.dao;

import flowershop.models.Supplier;
import flowershop.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SupplierDAO {

    // Lấy tất cả supplier
    public List<Supplier> getAllSuppliers() {

        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Supplier> list =
                session.createQuery("FROM Supplier", Supplier.class).list();

        session.close();

        return list;
    }

    // Thêm supplier
    public void save(Supplier supplier) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {

            tx = session.beginTransaction();

            session.save(supplier);

            tx.commit();

        } catch (Exception e) {

            if (tx != null) tx.rollback();
            e.printStackTrace();

        } finally {

            session.close();

        }
    }

    // Cập nhật supplier
    public void update(Supplier supplier) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {

            tx = session.beginTransaction();

            session.update(supplier);

            tx.commit();

        } catch (Exception e) {

            if (tx != null) tx.rollback();
            e.printStackTrace();

        } finally {

            session.close();

        }
    }

    // Xóa supplier
    public void delete(Supplier supplier) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {

            tx = session.beginTransaction();

            session.delete(supplier);

            tx.commit();

        } catch (Exception e) {

            if (tx != null) tx.rollback();
            e.printStackTrace();

        } finally {

            session.close();

        }
    }

}
