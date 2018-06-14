package dao;

import entity.reg.UserAccount;
import entity.staging.Login;


public interface UserDao {

    public int register(UserAccount u);


    /*
     * Retrieve the Customer object from the database
     */
    public UserAccount validateCustomer(Login login);

}
