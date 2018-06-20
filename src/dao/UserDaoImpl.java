package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.DBUtils;
import db.DbManager;
import domain.Login;
import domain.User;
import util.Utilities;

public class UserDaoImpl implements UserDao {
    private static final Logger LOG = Logger.getLogger(UserDaoImpl.class.getName());

    private DbManager db = new DbManager();

    /**
     * Attempts to create a new User, returns the User if it succeeds or null if it fails
     */
    @Override
    public User register(User user) {
        Connection conn = null;
        PreparedStatement ps = null;

        int status = 0;

        try {
            // update the password with its hashed version
            user.setPassword(Utilities.hash(user.getPassword()));

            conn = db.getConnection();
            ps = conn.prepareStatement("INSERT INTO user " //
                    + "(user_id, username, password, full_name, recover_password_question, recover_password_answer, balance) " //
                    + "value (?, ?, ?, ?, ?, ?, ?);");

            int i = 1; // sql param index

            ps.setLong(i++, 0L); // auto-increment user_id
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getPassword());
            ps.setString(i++, user.getName());
            ps.setString(i++, user.getRecoverPasswordQuestion());
            ps.setString(i++, user.getRecoverPasswordAnswer());
            ps.setBigDecimal(i++, new BigDecimal(0)); // initial starting balance of 0

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
     * Validates the Login and returns the User if it succeeds or null on failure.
     */
    @Override
    public User validateCustomer(Login login) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        User user = null;

        try {
            conn = db.getConnection();
            ps = conn.prepareStatement("SELECT user_id, username, full_name, recover_password_question, balance FROM user WHERE username = ? AND password = ?");
            ps.setString(1, login.getUsername());
            ps.setString(2, Utilities.hash(login.getPassword()));

            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
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
