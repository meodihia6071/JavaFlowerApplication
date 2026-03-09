package flowershop.dao;

import flowershop.models.User;

public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }
}