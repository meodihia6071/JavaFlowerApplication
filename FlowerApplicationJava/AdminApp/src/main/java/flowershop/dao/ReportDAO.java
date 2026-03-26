package flowershop.dao;

import flowershop.utils.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.*;

public class ReportDAO {

    // ================= COUNT =================
    public int countProducts(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "select count(p) from Product p", Long.class
            ).getSingleResult();
            return count.intValue();
        }
    }

    public int countOrders(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "select count(o) from Order o", Long.class
            ).getSingleResult();
            return count.intValue();
        }
    }

    public int countCustomers(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "select count(c) from Customer c", Long.class
            ).getSingleResult();
            return count.intValue();
        }
    }

    // ================= REVENUE =================
    public double getRevenueByMonth(int month){

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            BigDecimal total = session.createQuery(
                            "select sum(o.total) from Order o where month(o.orderDate)=:m",
                            BigDecimal.class
                    )
                    .setParameter("m", month)
                    .uniqueResult();

            return total == null ? 0 : total.doubleValue();
        }
    }

    // ================= BAR CHART =================
    public Map<Integer, Double> getRevenueGroupByMonth(){

        Map<Integer, Double> result = new HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = """
                SELECT MONTH(o.orderDate), SUM(o.total)
                FROM Order o
                GROUP BY MONTH(o.orderDate)
            """;

            List<Object[]> list = session.createQuery(hql).list();

            for(Object[] row : list){
                int month = ((Number) row[0]).intValue();
                double total = ((Number) row[1]).doubleValue();
                result.put(month, total);
            }
        }

        return result;
    }

    // ================= LINE CHART =================
    public Map<String, Integer> getOrdersGroupByWeek(){

        Map<String, Integer> result = new LinkedHashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = """
                SELECT WEEK(o.orderDate), COUNT(o.orderId)
                FROM Order o
                GROUP BY WEEK(o.orderDate)
            """;

            List<Object[]> list = session.createQuery(hql).list();

            for(Object[] row : list){
                String week = "Week " + row[0];
                int count = ((Number) row[1]).intValue();
                result.put(week, count);
            }
        }

        return result;
    }

    // ================= PIE CHART =================
    public Map<String, Integer> getTopProducts(){

        Map<String, Integer> result = new HashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = """
                SELECT p.productName, SUM(od.quantity)
                FROM OrderDetail od
                JOIN od.product p
                GROUP BY p.productName
                ORDER BY SUM(od.quantity) DESC
            """;

            List<Object[]> list = session.createQuery(hql)
                    .setMaxResults(5)
                    .list();

            for(Object[] row : list){
                result.put((String) row[0], ((Number) row[1]).intValue());
            }
        }

        return result;
    }

    // ================= TOP CUSTOMERS =================
    public List<Map<String, Object>> getTopCustomers(){

        List<Map<String, Object>> result = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = """
                SELECT c.customerName, COUNT(o.orderId), SUM(o.total)
                FROM Order o
                JOIN o.customer c
                GROUP BY c.customerName
                ORDER BY SUM(o.total) DESC
            """;

            List<Object[]> list = session.createQuery(hql)
                    .setMaxResults(5)
                    .list();

            for(Object[] row : list){
                Map<String, Object> map = new HashMap<>();
                map.put("name", row[0]);
                map.put("orders", row[1]);
                map.put("spent", row[2]);
                result.add(map);
            }
        }

        return result;
    }

    // ================= LOW STOCK =================
    public List<Map<String, Object>> getLowStockProducts(){

        List<Map<String, Object>> result = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = """
                SELECT p.productName, p.quantity
                FROM Product p
                WHERE p.quantity < 10
            """;

            List<Object[]> list = session.createQuery(hql).list();

            for(Object[] row : list){
                Map<String, Object> map = new HashMap<>();
                map.put("product", row[0]);
                map.put("quantity", row[1]);
                result.add(map);
            }
        }

        return result;
    }
}