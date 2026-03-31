package flowershop.dao;

import flowershop.models.Category;
import flowershop.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CategoryDAO {

    // GET ALL
    public List<Category> getAllCategories(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Category> list = session.createQuery("FROM Category", Category.class).list();
        session.close();
        return list;
    }

    // SAVE
    public void save(Category category){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.save(category);

        tx.commit();
        session.close();
    }

    // UPDATE
    public void update(Category category){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.update(category);

        tx.commit();
        session.close();
    }

    // DELETE
    public boolean isCategoryUsed(int categoryId) {
        try (org.hibernate.Session session = flowershop.utils.HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :catId";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("catId", categoryId)
                    .uniqueResult();

            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    public void delete(Category category){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.delete(category);

        tx.commit();
        session.close();
    }
}