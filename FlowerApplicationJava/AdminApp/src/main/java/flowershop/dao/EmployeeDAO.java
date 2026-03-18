package flowershop.dao;

import flowershop.models.Employee;
import flowershop.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EmployeeDAO {

    // ===== GET ALL =====
    public List<Employee> getAllEmployees() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Employee> list = session.createQuery("FROM Employee", Employee.class).list();
        session.close();

        return list;
    }

    // ===== SAVE =====
    public void save(Employee e) {

        Transaction tx = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            tx = session.beginTransaction();
            session.persist(e);
            tx.commit();

        } catch (Exception ex){
            if(tx != null) tx.rollback();
            ex.printStackTrace();
        }
    }

    // ===== UPDATE =====
    public void update(Employee e) {

        Transaction tx = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            tx = session.beginTransaction();
            session.merge(e);
            tx.commit();

        } catch (Exception ex){
            if(tx != null) tx.rollback();
            ex.printStackTrace();
        }
    }

    // ===== DELETE =====
    public void delete(Employee e) {

        Transaction tx = null;

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            tx = session.beginTransaction();
            session.remove(session.contains(e) ? e : session.merge(e));
            tx.commit();

        } catch (Exception ex){
            if(tx != null) tx.rollback();
            ex.printStackTrace();
        }
    }

    // ===== FIND BY ID (optional) =====
    public Employee findById(int id){

        Session session = HibernateUtil.getSessionFactory().openSession();
        Employee e = session.get(Employee.class, id);
        session.close();

        return e;
    }
}