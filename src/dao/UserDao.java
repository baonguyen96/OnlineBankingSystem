package dao;

import domain.Login;
import domain.UserAccount;

public interface UserDao {

    public UserAccount register(UserAccount u);

    /*
     * Retrieve the Customer object from the database
     */
    public UserAccount validateCustomer(Login login);

}
