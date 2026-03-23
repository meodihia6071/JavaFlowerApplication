package flowershop.dao;

import flowershop.utils.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.*;

public class ReportDAO {

    // ================= BASIC =================

    public int countProducts(){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Number count = (Number) session.createNativeQuery(
                    "SELECT COUNT(*) FROM products"
            ).getSingleResult();
            return count.intValue();
        }
    }

    // 🔥 OVERLOAD (có filter)
    public int countOrders(LocalDate from, LocalDate to){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            String sql = "SELECT COUNT(*) FROM orders WHERE order_date BETWEEN :from AND :to";

            Number count = (Number) session.createNativeQuery(sql)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getSingleResult();

            return count.intValue();
        }
    }

    // 🔁 method cũ (không filter)
    public int countOrders(){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Number count = (Number) session.createNativeQuery(
                    "SELECT COUNT(*) FROM orders"
            ).getSingleResult();
            return count.intValue();
        }
    }

    public int countCustomers(){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Number count = (Number) session.createNativeQuery(
                    "SELECT COUNT(*) FROM customers"
            ).getSingleResult();
            return count.intValue();
        }
    }

    // ================= REVENUE =================

    // 🔥 có filter
    public double getRevenueByMonth(int month, LocalDate from, LocalDate to){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            String sql = """
                SELECT SUM(total)
                FROM orders
                WHERE MONTH(order_date) = :m
                AND order_date BETWEEN :from AND :to
            """;

            Number total = (Number) session.createNativeQuery(sql)
                    .setParameter("m", month)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getSingleResult();

            return total == null ? 0 : total.doubleValue();
        }
    }

    // 🔁 không filter
    public double getRevenueByMonth(int month){
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            Number total = (Number) session.createNativeQuery(
                    "SELECT SUM(total) FROM orders WHERE MONTH(order_date) = :m"
            ).setParameter("m", month).getSingleResult();

            return total == null ? 0 : total.doubleValue();
        }
    }

    public double getRevenueByDate(LocalDate from, LocalDate to){
        Session session = HibernateUtil.getSessionFactory().openSession();

        Number total = (Number) session.createNativeQuery(
                        "SELECT SUM(total) FROM orders WHERE order_date BETWEEN :from AND :to"
                )
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();

        session.close();

        return total == null ? 0 : total.doubleValue();
    }

    // ================= TREND =================

    public Map<String, Integer> getOrdersTrend(LocalDate from, LocalDate to){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            String sql = "SELECT DAY(order_date), COUNT(*) FROM orders ";

            if(from != null && to != null){
                sql += "WHERE order_date BETWEEN :from AND :to ";
            }

            sql += "GROUP BY DAY(order_date) ORDER BY DAY(order_date)";

            var query = session.createNativeQuery(sql);

            if(from != null && to != null){
                query.setParameter("from", from);
                query.setParameter("to", to);
            }

            List<Object[]> result = query.getResultList();

            Map<String, Integer> map = new LinkedHashMap<>();

            for(Object[] row : result){
                String day = "Day " + row[0];
                Integer count = ((Number) row[1]).intValue();
                map.put(day, count);
            }

            return map;
        }
    }

    // ================= RECENT ORDERS =================

    // 🔥 có filter
    public List<Map<String, Object>> getRecentOrders(LocalDate from, LocalDate to){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            String sql = """
                SELECT o.order_id, c.customer_name, o.order_date, o.total, o.status
                FROM orders o
                JOIN customers c ON o.customer_id = c.customer_id
                WHERE o.order_date BETWEEN :from AND :to
                ORDER BY o.order_date DESC
                LIMIT 5
            """;

            List<Object[]> list = session.createNativeQuery(sql)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();

            return mapOrders(list);
        }
    }

    // 🔁 không filter
    public List<Map<String, Object>> getRecentOrders(){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            List<Object[]> list = session.createNativeQuery(
                    """
                    SELECT o.order_id, c.customer_name, o.order_date, o.total, o.status
                    FROM orders o
                    JOIN customers c ON o.customer_id = c.customer_id
                    ORDER BY o.order_date DESC LIMIT 5
                    """
            ).getResultList();

            return mapOrders(list);
        }
    }

    // ================= TOP CUSTOMERS =================

    // 🔥 có filter
    public List<Map<String, Object>> getTopCustomers(LocalDate from, LocalDate to){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            String sql = """
                SELECT c.customer_name, COUNT(o.order_id), SUM(o.total)
                FROM customers c
                JOIN orders o ON c.customer_id = o.customer_id
                WHERE o.order_date BETWEEN :from AND :to
                GROUP BY c.customer_name
                ORDER BY SUM(o.total) DESC
                LIMIT 5
            """;

            List<Object[]> list = session.createNativeQuery(sql)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();

            return mapCustomers(list);
        }
    }

    // 🔁 không filter
    public List<Map<String, Object>> getTopCustomers(){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            List<Object[]> list = session.createNativeQuery(
                    """
                    SELECT c.customer_name, COUNT(o.order_id), SUM(o.total)
                    FROM customers c
                    JOIN orders o ON c.customer_id = o.customer_id
                    GROUP BY c.customer_name
                    ORDER BY SUM(o.total) DESC
                    LIMIT 5
                    """
            ).getResultList();

            return mapCustomers(list);
        }
    }

    // ================= LOW STOCK =================

    public List<Map<String, Object>> getLowStock(){

        try(Session session = HibernateUtil.getSessionFactory().openSession()){

            List<Object[]> list = session.createNativeQuery(
                    "SELECT product_name, quantity FROM stock WHERE quantity < 10"
            ).getResultList();

            List<Map<String, Object>> result = new ArrayList<>();

            for(Object[] row : list){
                Map<String, Object> map = new HashMap<>();
                map.put("product", row[0]);
                map.put("qty", ((Number)row[1]).intValue());
                result.add(map);
            }

            return result;
        }
    }

    // ================= MAPPERS =================

    private List<Map<String, Object>> mapOrders(List<Object[]> list){
        List<Map<String, Object>> result = new ArrayList<>();

        for(Object[] row : list){
            Map<String, Object> map = new HashMap<>();
            map.put("id", row[0]);
            map.put("customer", row[1]);
            map.put("date", row[2]);
            map.put("total", row[3]);
            map.put("status", row[4]);
            result.add(map);
        }

        return result;
    }

    private List<Map<String, Object>> mapCustomers(List<Object[]> list){
        List<Map<String, Object>> result = new ArrayList<>();

        for(Object[] row : list){
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("orders", ((Number)row[1]).intValue());
            map.put("spent", row[2]);
            result.add(map);
        }

        return result;
    }
}