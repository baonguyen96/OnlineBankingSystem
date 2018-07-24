package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import core.DBUtils;
import core.Logger;
import db.DbManager;
import domain.Account;
import domain.User;

public class AccountDaoImpl {
    private static final Logger LOG = new Logger(AccountDaoImpl.class);

    private DbManager db = new DbManager();

    /**
     * Attempts to create a new Account, returns the Account if it succeeds or null if it fails
     * @throws Exception 
     */
    public Account createAccount(Account account) throws Exception {
        LOG.log(Logger.Action.BEGIN, "account");
        try {
            if (account == null) throw new Exception("cannot create a null account");
            if (account.getUser() == null) throw new Exception("cannot create an account with a null user");

            Connection conn = null;
            PreparedStatement ps = null;

            int status = 0;

            try {
                conn = db.getConnection();

                ps = conn.prepareStatement("INSERT INTO account " //
                        + "(id, user_id, name, balance, created_on, updated_on) " //
                        + "value (?, ?, ?, ?, now(), now())");

                int i = 1; // sql param index

                ps.setLong(i++, account.hashCode()); // auto-increment id
                ps.setLong(i++, account.getUser().hashCode());
                ps.setString(i++, account.getName());
                ps.setBigDecimal(i++, new BigDecimal(0));

                status = ps.executeUpdate();

                if (status == 1) {
                    account = getAccountById(conn, account.getUser(), account.hashCode());
                    LOG.info("Created new account for username:" + account.getUser().getUsername() + "; account: " + account.toString());
                } else {
                    LOG.warn("Failed to create account for username " + account.getUser().getUsername());
                    account = null;
                }
            } catch (Exception e) {
                account = null;
                LOG.error("Error validating customer login", e);
            } finally {
                DBUtils.closeQuietly(ps, conn);
            }
        } finally {
            LOG.log(Logger.Action.RETURN, "account");
        }
        return account;
    }

    /**
     * Loads the user's account information onto their User object
     */
    public User loadAccounts(User user) {
        LOG.log(Logger.Action.BEGIN, "user");

        LOG.info("loading accounts for user: " + user.hashCode());

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
            ps.setLong(1, user.hashCode());

            rs = ps.executeQuery();

            List<Account> accounts = new ArrayList<Account>();
            while (rs.next()) {
                Account account = new Account();
                account.setUser(user);
                account.setName(rs.getString("name"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCreatedOn(rs.getDate("created_on"));
                account.setUpdatedOn(rs.getDate("updated_on"));
                accounts.add(account);

                LOG.info("loaded account " + account.hashCode() + " + for user: " + user.hashCode());
            }
            user.setAccounts(accounts);

        } catch (Exception e) {
            LOG.error("Error validating customer login", e);
        } finally {
            DBUtils.closeQuietly(rs, ps, conn);
        }
        LOG.log(Logger.Action.RETURN, "userWithAccounts");
        return user;
    }

    private Account getAccountById(Connection conn, User user, int accountHashCode) {
        LOG.log(Logger.Action.BEGIN, "dbConnection", "user", "accountHashCode");
        LOG.info("loading account by id: " + accountHashCode);

        PreparedStatement ps = null;
        ResultSet rs = null;
        Account account = null;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement("SELECT " // 
                    + " id, user_id, name, balance, created_on, updated_on" //
                    + " FROM account" // 
                    + " WHERE id = ?");
            ps.setLong(1, accountHashCode);

            rs = ps.executeQuery();

            if (rs.next() && user.hashCode() == rs.getInt("user_id")) {
                account = new Account();
                account.setUser(user);
                account.setName(rs.getString("name"));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setCreatedOn(rs.getDate("created_on"));
                account.setUpdatedOn(rs.getDate("updated_on"));
                user.addAccount(account);
                LOG.info("loaded account: " + account.hashCode());
            }

        } catch (Exception e) {
            LOG.error("Error validating customer login", e);
        } finally {
            DBUtils.closeQuietly(rs, ps);
        }

        LOG.log(Logger.Action.RETURN, "account");
        return account;
    }

    void updateBalance(Account account) {
        LOG.log(Logger.Action.BEGIN, "account");
        LOG.info("updating balance for account_id: " + account);

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
            ps.setLong(1, account.hashCode());
            ps.executeUpdate();

            DBUtils.closeQuietly(ps);
            Account tmpAccount = getAccountById(conn, account.getUser(), account.hashCode());
            account.setBalance(tmpAccount.getBalance());
            account.setUpdatedOn(tmpAccount.getUpdatedOn());
        } catch (Exception e) {
            LOG.error("Error updating account balance", e);
        } finally {
            DBUtils.closeQuietly(ps, conn);
        }
        LOG.log(Logger.Action.RETURN);
    }
}
