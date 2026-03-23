package flowershop.services;

import flowershop.dao.ReportDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    private LocalDate fromDate;
    private LocalDate toDate;

    // ================= FILTER =================

    public void setDateRange(LocalDate from, LocalDate to){
        this.fromDate = from;
        this.toDate = to;
    }

    private boolean hasFilter(){
        return fromDate != null && toDate != null;
    }

    // ================= BASIC =================

    public int countProducts(){
        return reportDAO.countProducts();
    }

    public int countOrders(){
        return hasFilter()
                ? reportDAO.countOrders(fromDate, toDate)
                : reportDAO.countOrders();
    }

    public int countCustomers(){
        return reportDAO.countCustomers();
    }

    public double getRevenue(int month){
        return hasFilter()
                ? reportDAO.getRevenueByMonth(month, fromDate, toDate)
                : reportDAO.getRevenueByMonth(month);
    }

    // ================= KPI =================

    public double getTotalRevenue(){
        if(fromDate != null && toDate != null){
            return reportDAO.getRevenueByDate(fromDate, toDate);
        }

        double total = 0;
        for(int i = 1; i <= 12; i++){
            total += getRevenue(i);
        }
        return total;
    }

    public double getAverageOrderValue(){
        int orders = countOrders();
        if(orders == 0) return 0;
        return getTotalRevenue() / orders;
    }

    // ================= ADVANCED =================

    public Map<String, Integer> getOrdersTrend(){

        LocalDate from = this.fromDate;
        LocalDate to = this.toDate;

        // 👉 fallback nếu chưa filter
        if(from == null || to == null){
            to = LocalDate.now();
            from = to.minusDays(7);
        }

        return reportDAO.getOrdersTrend(from, to);
    }

    public List<Map<String, Object>> getRecentOrders(){
        return hasFilter()
                ? reportDAO.getRecentOrders(fromDate, toDate)
                : reportDAO.getRecentOrders();
    }

    public List<Map<String, Object>> getTopCustomers(){
        return hasFilter()
                ? reportDAO.getTopCustomers(fromDate, toDate)
                : reportDAO.getTopCustomers();
    }

    public List<Map<String, Object>> getLowStock(){
        return reportDAO.getLowStock(); // không cần filter
    }
}