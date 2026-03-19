package flowershop.dao;

import flowershop.utils.HibernateUtil;
import org.hibernate.Session;
import java.math.BigDecimal;

public class ReportDAO {

    public int countProducts(){

        Session session = HibernateUtil.getSessionFactory().openSession();

        Long count = session.createQuery(
                "select count(p) from Product p", Long.class
        ).getSingleResult();

        session.close();

        return count.intValue();
    }

    public int countOrders(){

        Session session = HibernateUtil.getSessionFactory().openSession();

        Long count = session.createQuery(
                "select count(o) from Order o", Long.class
        ).getSingleResult();

        session.close();

        return count.intValue();
    }

    public int countCustomers(){

        Session session = HibernateUtil.getSessionFactory().openSession();

        Long count = session.createQuery(
                "select count(c) from Customer c", Long.class
        ).getSingleResult();

        session.close();

        return count.intValue();
    }

    public double getRevenueByMonth(int month){

        Session session = HibernateUtil.getSessionFactory().openSession();

        BigDecimal total = session.createQuery(
                        "select sum(o.total) from Order o where month(o.orderDate)=:m",
                        BigDecimal.class
                )
                .setParameter("m", month)
                .uniqueResult();

        session.close();

        if(total == null){
            return 0;
        }

        return total.doubleValue();
    }
}