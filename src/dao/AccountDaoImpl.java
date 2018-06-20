package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.DBUtils;
import db.DbManager;
import domain.Account;
import domain.User;

public class AccountDaoImpl {
    private static final Logger LOG = Logger.getLogger(AccountDaoImpl.class.getName());

    private DbManager db = new DbManager();

    /**
     * Attempts to create a new Account, returns the Account if it succeeds or null if it fails
     */
    public Account createAccount(Account account) {
        Connection conn = null;
        PreparedStatement ps = null;

        int status = 0;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement("INSERT INTO account " //
                    + "(id, user_id, name, balance, created_on, updated_on) " //
                    + "value (?, ?, ?, ?, now(), now())");

            int i = 1; // sql param index

            ps.setLong(i++, 0L); // auto-increment id
            ps.setLong(i++, account.getUserId());
            ps.setString(i++, account.getName());
            ps.setBigDecimal(i++, new BigDecimal(0));

            status = ps.executeUpdate();

            if (status == 1) {
                account.setId(DBUtils.getLastInsertId(conn));

                LOG.log(Level.INFO, "Created new account for user id:" + account.getUserId() + "; account: " + account.toString());
            } else {
                LOG.log(Level.INFO, "Failed to create account for user " + account.getUserId());
                account = null;
            }
        } catch (Exception e) {
            account = null;
            LOG.log(Level.SEVERE, "Error validating customer login", e);
        } finally {
            DBUtils.closeQuietly(ps, conn);
        }

        return account;
    }

    /**
     * Loads the user's account information onto their User object
     */
    public User loadAccounts(User user) {
        LOG.info("loading accounts for user: " + user.getId());

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement("SELECT " // 
                    + " id, user_id, name, balance, created_on, updated_on" //
                    + " FROM account" // 
                    + " WHERE user_id = ?" //
                    + " ORDER BY name ASC");
            ps.setLong(1, user.getId());

            rs = ps.executeQuery();

            List<Account> accounts = new ArrayList<Account>();
            while (rs.next()) {
                Account account = new Account();
                account.setId(rs.getLong("id"));
                account.setUserId(rs.getLong("user_id"));
                account.setName(rs.getString("name"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCreatedOn(rs.getDate("created_on"));
                account.setUpdatedOn(rs.getDate("updated_on"));
                accounts.add(account);

                LOG.info("loaded account " + account.getId() + " + for user: " + user.getId());
            }
            user.setAccounts(accounts);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error validating customer login", e);
        } finally {
            DBUtils.closeQuietly(rs, ps, conn);
        }
        return user;
    }

    public void updateBalance(Long account_id) {
        LOG.info("updating balance for account_id: " + account_id);

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getConnection();
            String sql = "UPDATE" //
                    + " account" //
                    + " inner join" //
                    + "     (select account_id, COALESCE(sum(amount), 0) as balance from transaction where account_id = ?) currBal" //
                    + "     on account.id = currBal.account_id"//
                    + " set account.balance = currBal.balance";
            System.out.println(sql);
            ps = conn.prepareStatement(sql);
            ps.setLong(1, account_id);
            ps.executeUpdate();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error updating account balance", e);
        } finally {
            DBUtils.closeQuietly(ps, conn);
        }
    }
}
