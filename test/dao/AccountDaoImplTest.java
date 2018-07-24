package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.DBUtils;
import db.DbManager;
import domain.Account;
import domain.User;

public class AccountDaoImplTest {

    DbManager dbManager;
    User user;

    @BeforeEach
    public void beforeEach() throws Exception {
        dbManager = new DbManager();

        resetDatabase();
        System.out.println(user);
        user = createUser();
        System.out.println(user);
    }

    public void resetDatabase() throws Exception {
        Connection conn = dbManager.getConnection();
        Statement statement = null;

        statement = conn.createStatement();
        statement.addBatch("SET FOREIGN_KEY_CHECKS = 0;");
        statement.addBatch("TRUNCATE table user;");
        statement.addBatch("TRUNCATE table account;");
        statement.addBatch("TRUNCATE table transaction;");
        statement.addBatch("SET FOREIGN_KEY_CHECKS = 1;");
        statement.executeBatch();
        DBUtils.closeQuietly(statement);
    }

    @Test
    public void createAccountTestSuccess() throws Exception {
        Account account = new Account();
        account.setName("accountName");
        account.setUser(user);
        user.addAccount(account);

        AccountDao accountDao = new AccountDaoImpl();
        accountDao.createAccount(account);

        Connection conn = dbManager.getConnection();
        PreparedStatement ps = conn.prepareStatement( //
                "select count(*) from account where id=? AND user_id=? AND name=? AND balance=?");

        ps.setLong(1, account.hashCode()); // auto-increment id
        ps.setLong(2, account.getUser().hashCode());
        ps.setString(3, account.getName());
        ps.setBigDecimal(4, new BigDecimal(0));

        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        DBUtils.closeQuietly(rs, ps, conn);

        assertEquals(1, count);
    }

    @Test
    public void loadAccountsTestSuccess() throws Exception {
        Account account = new Account();
        account.setName("accountName");
        account.setUser(user);
        account.setBalance(new BigDecimal(0));
        account.getBalance().setScale(2);
        user.addAccount(account);

        AccountDao accountDao = new AccountDaoImpl();
        accountDao.createAccount(account);

        user = accountDao.loadAccounts(user);

        assertAccount(account, user.getAccounts().iterator().next());
    }

    @Test
    public void getAccountByIdTestSuccess() throws Exception {
        Account account = new Account();
        account.setName("accountName");
        account.setUser(user);
        account.setBalance(new BigDecimal(0));
        account.getBalance().setScale(2);
        user.addAccount(account);

        AccountDaoImpl accountDao = new AccountDaoImpl();
        accountDao.createAccount(account);

        Connection conn = dbManager.getConnection();
        Account retval = accountDao.getAccountById(conn, user, account.hashCode());

        assertAccount(account, retval);
    }

    private User createUser() {
        User rawUser = new User();
        rawUser.setName("FirstRegister LastRegister");
        rawUser.setUsername("usernameRegister");
        rawUser.setPassword("passwordRegister");
        rawUser.setRecoverPasswordQuestion("questionRegister");
        rawUser.setRecoverPasswordAnswer("answerRegister");

        UserDaoImpl userDao = new UserDaoImpl();
        return userDao.register(rawUser);
    }

    private void assertAccount(Account expected, Account actual) {
        if (expected != null) assertNotNull(actual);
        assertEquals(expected.getBalance() == null, actual.getBalance() == null);
        if (expected.getBalance() != null) assertEquals(expected.getBalance().doubleValue(), actual.getBalance().doubleValue());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStatus(), actual.getStatus());
        if (expected.getTransactions() == null) {
            assertNull(actual.getTransactions());
        } else {
            assertNotNull(actual.getTransactions());
            assertEquals(expected.getTransactions().size(), actual.getTransactions().size());
        }
    }

}
