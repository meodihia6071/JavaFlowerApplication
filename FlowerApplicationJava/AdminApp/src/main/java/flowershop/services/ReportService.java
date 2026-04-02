package flowershop.services;

import flowershop.dao.ReportDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    // ================= BASIC =================
    public int countOrders(LocalDate start, LocalDate end)
    {
        return reportDAO.countOrders(start, end);
    }

    public int countCustomers(LocalDate start, LocalDate end) {
        return reportDAO.countCustomers(start, end);
    }

    public int countProducts() {
        return reportDAO.countProducts();
    }

    // ================= REVENUE =================
    public double getTotalRevenue(LocalDate start, LocalDate end){
        return reportDAO.getTotalRevenue(start, end);
    }

    public double getTotalCost(LocalDate start, LocalDate end){
        return reportDAO.getTotalCost(start, end);
    }

    public double getAverageOrderValue(LocalDate start, LocalDate end){
        return reportDAO.getAverageOrderValue(start, end);
    }

    // ================= CHART =================
    public Map<String, Double> getRevenueByMonth() {
        return reportDAO.getRevenueLast12Months();
    }

    public Map<String, Integer> getOrdersByLast10Weeks() {
        return reportDAO.getOrdersByLast10Weeks();
    }

    public Map<String, Integer> getTopProducts(LocalDate start, LocalDate end){
        return reportDAO.getTopProducts(start, end);
    }

    // ================= TABLE =================
    public List<Map<String, Object>> getTopCustomers() {
        return reportDAO.getTopCustomers();
    }

    public List<Map<String, Object>> getLowStock(){
        return reportDAO.getLowStockProducts();
    }
}