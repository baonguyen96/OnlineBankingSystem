package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.DBUtils;
import db.DbManager;
import domain.Login;
import domain.UserAccount;
import util.Utilities;

public class UserDaoImpl implements UserDao {
    private static final Logger LOG = Logger.getLogger(UserDaoImpl.class.getName());

    private DbManager db = new DbManager();

    /**
     * Attempts to create a new UserAccount, returns the UserAccount if it succeeds or null if it fails
     */
    @Override
    public UserAccount register(UserAccount user) {
        Connection conn = null;
        PreparedStatement ps = null;

        int status = 0;

        try {
            // update the password with its hashed version
            user.setPassword(Utilities.hash(user.getPassword()));

            conn = db.getConnection();
            ps = conn.prepareStatement("INSERT INTO user_accounts " + "(username, password, full_name, recover_password_question, recover_password_answer, balance) " + "value ('?', '?', '?', '', '', 0);");
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getRecoverPasswordQuestion());
            ps.setString(5, user.getRecoverPasswordAnswer());
            status = ps.executeUpdate();

            if (status == 1) {
                user.setUserID(DBUtils.getLastInsertId(conn));

                // Clearing because security!
                user.setPassword(null);
                user.setRecoverPasswordAnswer(null);

                LOG.log(Level.INFO, "Registered user " + user.getUsername() + " with id " + user.getUserID());
            } else {
                LOG.log(Level.INFO, "Failed to register user " + user.getUsername());
                user = null;
            }
        } catch (Exception e) {
            user = null;
            LOG.log(Level.SEVERE, "Error validating customer login", e);
        } finally {
            DBUtils.closeQuietly(ps, conn);
        }

        return user;
    }

    /**
     * Validates the Login and returns the UserAccount if it succeeds or null on failure.
     */
    @Override
    public UserAccount validateCustomer(Login login) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        UserAccount user = null;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement("SELECT user_id, username, full_name, recover_password_question, balance FROM user_accounts WHERE username = ? AND password = ?");
            ps.setString(1, login.getUsername());
            ps.setString(2, Utilities.hash(login.getPassword()));

            rs = ps.executeQuery();
            if (rs.next()) {
                user = new UserAccount();
                user.setUserID(rs.getLong("user_id"));
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("full_name"));
                user.setRecoverPasswordQuestion(rs.getString("recover_password_question"));
                user.setBalance(rs.getDouble("balance"));
                // It's bad security to load these back out of the database
                // user.setPassword(rs.getString("password"));
                // user.setRecoverPasswordAnswer(rs.getString("recover_password_answer"));
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error validating customer login", e);
        } finally {
            DBUtils.closeQuietly(rs, ps, conn);
        }
        return user;
    }

}
