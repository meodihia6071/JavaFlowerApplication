package flowershop.dao;

import flowershop.models.Stock;
import flowershop.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class StockDAO {

    public List<Stock> getAllStock(){

        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Stock> list = session.createQuery("FROM Stock", Stock.class).list();

        session.close();

        return list;
    }


    public void save(Stock stock){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.save(stock);

        tx.commit();
        session.close();
    }


    public void update(Stock stock){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.update(stock);

        tx.commit();
        session.close();
    }


    public void delete(Stock stock){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.delete(stock);

        tx.commit();
        session.close();
    }

}
