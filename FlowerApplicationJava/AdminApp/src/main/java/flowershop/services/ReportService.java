package flowershop.services;

import flowershop.dao.ReportDAO;

import java.util.List;
import java.util.Map;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    // ================= BASIC =================
    public int countOrders(){
        return reportDAO.countOrders();
    }

    public int countCustomers(){
        return reportDAO.countCustomers();
    }

    public int countProducts(){
        return reportDAO.countProducts();
    }

    // ================= REVENUE =================
    public double getRevenue(int month){
        return reportDAO.getRevenueByMonth(month);
    }

    public double getTotalRevenue(){
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

    // ================= CHART =================

    // BarChart
    public Map<Integer, Double> getRevenueByMonth(){
        return reportDAO.getRevenueGroupByMonth();
    }

    // LineChart
    public Map<String, Integer> getOrdersByWeek(){
        return reportDAO.getOrdersGroupByWeek();
    }

    // PieChart
    public Map<String, Integer> getTopProducts(){
        return reportDAO.getTopProducts();
    }

    // ================= TABLE =================

    public List<Map<String, Object>> getTopCustomers(){
        return reportDAO.getTopCustomers();
    }

    public List<Map<String, Object>> getLowStock(){
        return reportDAO.getLowStockProducts();
    }
}