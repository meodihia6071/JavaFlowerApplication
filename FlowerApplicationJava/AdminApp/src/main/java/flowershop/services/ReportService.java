package flowershop.services;

import flowershop.dao.ReportDAO;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

        return reportDAO.countOrders();
    }

    public int countCustomers(){
        return reportDAO.countCustomers();
    }

    public double getRevenue(int month){
        return reportDAO.getRevenueByMonth(month);
    }

    // Tổng doanh thu cả năm
    public double getTotalRevenue(){
        double total = 0;

        for(int i = 1; i <= 12; i++){
            total += getRevenue(i);
        }

        return total;
    }

    // Giá trị trung bình mỗi đơn
    public double getAverageOrderValue(){
        int orders = countOrders();

        if(orders == 0) return 0;

        return getTotalRevenue() / orders;
    }
}