package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import core.DBUtils;
import core.Logger;
import db.DbManager;
import domain.Account;
import domain.Transaction;
import domain.TransactionType;

public class TransactionDaoImpl implements TransactionDao {
    private static final Logger LOG = new Logger(TransactionDaoImpl.class);

    private DbManager db = new DbManager();

    /**
     * Attempts to create a new Transaction, returns the Transaction if it succeeds or null if it fails
     * @throws Exception 
     */
    @Override
    public Transaction createTransaction(Transaction transaction) throws Exception {
        LOG.log(Logger.Action.BEGIN, "transaction");
        try {
            if (transaction == null) throw new Exception("cannot create a null transaction");
            if (transaction.getAccount() == null) throw new Exception("cannot create a transaction on a null account");
            if (!transaction.isValid()) throw new Exception("transaction is not valid");

            Connection conn = null;
            PreparedStatement ps = null;

            int status = 0;

            try {
                conn = db.getConnection();

                ps = conn.prepareStatement("INSERT INTO transaction " //
                        + "(id, account_id, type, amount, created_on, updated_on) " //
                        + "value (?, ?, ?, ?, ?, now())");

                int i = 1; // sql param index

                transaction.setCreatedOn(new Date());

                ps.setLong(i++, transaction.hashCode()); // auto-increment id
                ps.setLong(i++, transaction.getAccount().hashCode());
                ps.setString(i++, transaction.getType().toString());
                ps.setBigDecimal(i++, transaction.getAmount());
                ps.setTimestamp(i++, new Timestamp(transaction.getCreatedOn().getTime()));

                status = ps.executeUpdate();

                if (status == 1) {
                    LOG.info("Created new transaction for account id:" + transaction.getAccount().hashCode() + "; account: " + transaction.toString());
                    new AccountDaoImpl().updateBalance(transaction.getAccount());
                } else {
                    LOG.warn("Failed to create transaction for account " + transaction.getAccount().hashCode());
                    transaction = null;
                }
            } catch (Exception e) {
                transaction = null;
                LOG.error("Error validating customer login", e);
            } finally {
                DBUtils.closeQuietly(ps, conn);
            }
        } finally {
            LOG.log(Logger.Action.RETURN, "transaction");
        }
        return transaction;
    }

    /**
     * Loads the user's account information onto their User object
     */
    @Override
    public Account loadTransactions(Account account) {
        LOG.log(Logger.Action.BEGIN, "account");
        LOG.info("loading accounts for user: " + account.hashCode());

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement("SELECT " // 
                    + " id, account_id, type, amount, created_on, updated_on" //
                    + " FROM transaction" // 
                    + " WHERE account_id = ?" //
                    + " ORDER BY created_on DESC");
            ps.setLong(1, account.hashCode());

            rs = ps.executeQuery();

            List<Transaction> transactions = new ArrayList<Transaction>();
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setAccount(account);
                transaction.setType(TransactionType.valueOf(rs.getString("type")));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setCreatedOn(rs.getTimestamp("created_on"));
                transaction.setUpdatedOn(rs.getTimestamp("updated_on"));
                transactions.add(transaction);

                LOG.info("loaded account " + transaction.hashCode() + " + for user: " + transaction.hashCode());
            }
            account.setTransactions(transactions);

        } catch (Exception e) {
            LOG.error("Error validating customer login", e);
        } finally {
            DBUtils.closeQuietly(rs, ps, conn);
        }
        LOG.log(Logger.Action.RETURN, "account");
        return account;
    }

}
