package dao;

import domain.Account;
import domain.Transaction;

public interface TransactionDao {

    /**
     * Attempts to create a new Transaction, returns the Transaction if it succeeds or null if it fails
     * @throws Exception 
     */
    Transaction createTransaction(Transaction transaction) throws Exception;

    /**
     * Loads the user's account information onto their User object
     */
    Account loadTransactions(Account account);

}