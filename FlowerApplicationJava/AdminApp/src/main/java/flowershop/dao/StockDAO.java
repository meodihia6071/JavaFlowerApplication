package flowershop.dao;

import flowershop.models.Stock;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class StockDAO {

    public List<Stock> getAllStock() {
        //resources code tự động đóng Session
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = "SELECT s FROM Stock s " +
                    "LEFT JOIN FETCH s.product " +
                    "LEFT JOIN FETCH s.supplierEntity";
            return session.createQuery(hql, Stock.class).list();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi lấy danh sách Stock!");
            return null;
        }
    }

    public void save(Stock stock) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(stock);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void update(Stock stock) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.update(stock);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void delete(Stock stock) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.delete(stock);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}