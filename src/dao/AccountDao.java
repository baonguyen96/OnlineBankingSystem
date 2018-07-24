package dao;

import domain.Account;
import domain.User;

public interface AccountDao {

    /**
     * Attempts to create a new Account, returns the Account if it succeeds or null if it fails
     * @throws Exception 
     */
    Account createAccount(Account account) throws Exception;

    /**
     * Loads the user's account information onto their User object
     */
    User loadAccounts(User user);

}