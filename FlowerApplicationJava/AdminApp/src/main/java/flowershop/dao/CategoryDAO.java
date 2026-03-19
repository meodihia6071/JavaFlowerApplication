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
    public void delete(Category category){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.delete(category);

        tx.commit();
        session.close();
    }
}