package dao;

import domain.Login;
import domain.User;

public interface UserDao {

    public User register(User u);

    /*
     * Retrieve the Customer object from the database
     */
    public User validateUser(Login login);

}
