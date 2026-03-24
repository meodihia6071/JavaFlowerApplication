package flowershop.dao;

import flowershop.models.Stock;
import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class StockDAO {

    public List<Stock> getAllStock() {
        // Dùng try-with-resources để code tự động đóng Session dù có lỗi hay không
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // DÙNG JOIN FETCH: Lấy bảng Stock, tiện tay gom luôn dữ liệu bảng Product và Supplier đi kèm
            // Để lên màn hình có cái mà hiển thị tên Hoa và tên Nhà cung cấp
            String hql = "SELECT s FROM Stock s LEFT JOIN FETCH s.product LEFT JOIN FETCH s.supplier";
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
            if (tx != null) tx.rollback(); // Bị lỗi thì hoàn tác (Rollback) không cho lưu bậy
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