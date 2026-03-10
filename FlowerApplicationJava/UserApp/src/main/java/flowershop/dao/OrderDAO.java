package flowershop.dao;

import flowershop.models.Order;

public class OrderDAO extends BaseDAO<Order> {

    public OrderDAO() {
        super(Order.class);
    }
}