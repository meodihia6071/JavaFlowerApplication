package flowershop.dao;

import flowershop.utils.HibernateUtil;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ReportDAO {

    // ================= COUNT =================
    public int countProducts(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select count(p) from Product p", Long.class)
                    .getSingleResult().intValue();
        }
    }

    public int countOrders(LocalDate start, LocalDate end) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count;

            if (start != null && end != null) {
                // Có filter theo ngày
                String hql = "select count(o) from Order o where o.orderDate between :start and :end";
                count = session.createQuery(hql, Long.class)
                        .setParameter("start", start)
                        .setParameter("end", end)
                        .getSingleResult();
            } else {
                // All Time: không lọc theo ngày
                String hql = "select count(o) from Order o";
                count = session.createQuery(hql, Long.class)
                        .getSingleResult();
            }

            return count.intValue();
        }
    }
    public int countCustomers(LocalDate start, LocalDate end){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = "SELECT COUNT(DISTINCT c.customerId) " +
                    "FROM Order o " +
                    "JOIN o.customer c";

            if (start != null && end != null) {
                hql += "  WHERE o.orderDate BETWEEN :start AND :end";
            }

            var query = session.createQuery(hql, Long.class);

            if (start != null && end != null) {
                query.setParameter("start", start);
                query.setParameter("end", end);
            }

            Long count = query.uniqueResult();
            return count == null ? 0 : count.intValue();
        }
    }

    // ================= REVENUE =================
    public double getTotalRevenue(LocalDate start, LocalDate end) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BigDecimal total;

            if (start != null && end != null) {
                String hql = "select sum(o.total) from Order o where o.orderDate between :start and :end";
                total = session.createQuery(hql, BigDecimal.class)
                        .setParameter("start", start)
                        .setParameter("end", end)
                        .uniqueResult();
            } else {
                String hql = "select sum(o.total) from Order o";
                total = session.createQuery(hql, BigDecimal.class)
                        .uniqueResult();
            }

            return total == null ? 0 : total.doubleValue();
        }
    }

    public double getTotalCost(LocalDate start, LocalDate end) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = """
            SELECT SUM(od.quantity * si.importPrice)
            FROM OrderDetail od
            JOIN od.order o
            JOIN od.product p
            JOIN Stock si ON si.product.productId = p.productId
            WHERE si.importDate = (
                SELECT MAX(si2.importDate)
                FROM Stock si2
                WHERE si2.product.productId = p.productId
            )
        """;

            if (start != null && end != null) {
                hql += " AND o.orderDate BETWEEN :start AND :end";
            }

            var query = session.createQuery(hql, Double.class);

            if (start != null && end != null) {
                query.setParameter("start", start);
                query.setParameter("end", end);
            }

            Double total = query.uniqueResult();
            return total == null ? 0 : total;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getAverageOrderValue(LocalDate start, LocalDate end) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Long count;
            if (start != null && end != null) {
                String hql = "select count(o) from Order o where o.orderDate between :start and :end";
                count = session.createQuery(hql, Long.class)
                        .setParameter("start", start)
                        .setParameter("end", end)
                        .uniqueResult();
            } else {
                String hql = "select count(o) from Order o";
                count = session.createQuery(hql, Long.class)
                        .uniqueResult();
            }

            if (count == 0) return 0;
            double totalRevenue = getTotalRevenue(start, end);
            return totalRevenue / count;
        }
    }

    // ================= BAR CHART =================
    public Map<Integer, Double> getRevenueLast12Months() {
        Map<Integer, Double> result = new LinkedHashMap<>();

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusYears(1).plusMonths(1); // từ tháng này năm ngoái + 1 tháng đến tháng này năm nay

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = """
            SELECT MONTH(o.orderDate), SUM(o.total)
            FROM Order o
            WHERE o.orderDate BETWEEN :start AND :end
            GROUP BY MONTH(o.orderDate)
        """;

            List<Object[]> list = session.createQuery(hql)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .list();

            // Khởi tạo 12 tháng với giá trị 0
            LocalDate iter = start;
            for(int i = 0; i < 12; i++){
                result.put(iter.getMonthValue(), 0.0);
                iter = iter.plusMonths(1);
            }

            for(Object[] row : list){
                int month = ((Number) row[0]).intValue();
                double revenue = ((Number) row[1]).doubleValue();
                result.put(month, revenue);
            }
        }

        return result;
    }

    // ================= LINE CHART =================
    public Map<String, Integer> getOrdersByLast10Weeks() {
        Map<String, Integer> result = new LinkedHashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            LocalDate end = LocalDate.now();
            LocalDate start = end.minusWeeks(9).with(java.time.DayOfWeek.MONDAY); // 10 tuần gồm tuần hiện tại + 9 tuần trước

            String hql = """
            SELECT WEEK(o.orderDate), YEAR(o.orderDate), COUNT(o.orderId)
            FROM Order o
            WHERE o.orderDate BETWEEN :start AND :end
            GROUP BY YEAR(o.orderDate), WEEK(o.orderDate)
            ORDER BY YEAR(o.orderDate), WEEK(o.orderDate)
        """;

            List<Object[]> list = session.createQuery(hql)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .list();

            LocalDate temp = start;
            for (int i = 0; i < 10; i++) {
                int weekNum = temp.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                int yearNum = temp.getYear();
                String key = "Week " + weekNum + " - " + yearNum;
                result.put(key, 0);
                temp = temp.plusWeeks(1);
            }

            for (Object[] row : list) {
                int week = ((Number) row[0]).intValue();
                int year = ((Number) row[1]).intValue();
                long count = ((Number) row[2]).longValue();

                String key = "Week " + week + " - " + year;
                if (result.containsKey(key)) {
                    result.put(key, (int) count);
                }
            }
        }

        return result;
    }

    // ================= PIE =================
    public Map<String, Integer> getTopProducts(LocalDate start, LocalDate end) {
        Map<String, Integer> result = new LinkedHashMap<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = "SELECT p.productName, SUM(od.quantity) " +
                    "FROM OrderDetail od " +
                    "JOIN od.product p ";

            if (start != null && end != null) {
                hql += "JOIN od.order o WHERE o.orderDate BETWEEN :start AND :end ";
            }

            hql += "GROUP BY p.productName " +
                    "ORDER BY SUM(od.quantity) DESC";

            var query = session.createQuery(hql, Object[].class);

            if (start != null && end != null) {
                query.setParameter("start", start);
                query.setParameter("end", end);
            }

            query.setMaxResults(5);
            List<Object[]> list = query.list();

            for (Object[] row : list) {
                result.put((String) row[0], ((Number) row[1]).intValue());
            }
        }

        return result;
    }

    // ================= TOP CUSTOMERS =================
    public List<Map<String, Object>> getTopCustomers() {

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

            for (Object[] row : list) {
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