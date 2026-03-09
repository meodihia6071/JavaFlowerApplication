package flowershop.dao;

import flowershop.models.Customer;

public class CustomerDAO extends BaseDAO<Customer> {

    public CustomerDAO() {
        super(Customer.class);
    }
}