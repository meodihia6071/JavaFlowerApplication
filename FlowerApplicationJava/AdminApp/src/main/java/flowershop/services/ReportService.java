package flowershop.services;

import flowershop.dao.ReportDAO;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    public int countProducts(){
        return reportDAO.countProducts();
    }

    public int countOrders(){
        return reportDAO.countOrders();
    }

    public int countCustomers(){
        return reportDAO.countCustomers();
    }

    public double getRevenue(int month){
        return reportDAO.getRevenueByMonth(month);
    }
}
