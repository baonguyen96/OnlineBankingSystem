package dao;

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

public class UserDaoImpl {
    private static final Logger LOG = Logger.getLogger(UserDaoImpl.class.getName());

    private DbManager db = new DbManager();

    /**
     * Attempts to create a new User, returns the User if it succeeds or null if it fails
     */
    public User register(User user) {
        Connection conn = null;
        PreparedStatement ps = null;

        int status = 0;

        try {
            // update the password with its hashed version
            user.setPassword(Utilities.hash(user.getPassword()));

            conn = db.getConnection();
            ps = conn.prepareStatement("INSERT INTO user " //
                    + "(id, username, password, full_name, recover_password_question, recover_password_answer, created_on, updated_on) " //
                    + "value (?, ?, ?, ?, ?, ?, now(), now())");

            int i = 1; // sql param index

            ps.setLong(i++, user.hashCode());
            ps.setString(i++, user.getUsername());
            ps.setString(i++, user.getPassword());
            ps.setString(i++, user.getName());
            ps.setString(i++, user.getRecoverPasswordQuestion());
            ps.setString(i++, user.getRecoverPasswordAnswer());

            status = ps.executeUpdate();

            if (status == 1) {
                DBUtils.closeQuietly(ps);
                user = loadUser(conn, user.getUsername(), null);
                LOG.log(Level.INFO, "Registered user " + user.getUsername() + " with id " + user.hashCode());
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
    public User validateCustomer(Login login) {
        Connection conn = null;
        User user = null;

        if (login.getPassword() != null) {
            try {
                conn = db.getConnection();
                user = loadUser(conn, login.getUsername(), login.getPassword());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error validating customer login", e);
            } finally {
                DBUtils.closeQuietly(conn);
            }
        }
        return user;
    }

    private User loadUser(Connection conn, String username, String password) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        User user = null;

        try {
            String sql = "SELECT" //
                    + " id, username, full_name, recover_password_question, created_on, updated_on" //
                    + " FROM user" //
                    + " WHERE username = ? ";
            if (password != null) sql = sql + " AND password = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            if (password != null) ps.setString(2, Utilities.hash(password));

            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUsername(rs.getString("username"));
                user.setName(rs.getString("full_name"));
                user.setRecoverPasswordQuestion(rs.getString("recover_password_question"));
                user.setCreatedOn(rs.getDate("created_on"));
                user.setUpdatedOn(rs.getDate("updated_on"));

                // It's bad security to load these back out of the database
                // user.setPassword(rs.getString("password"));
                // user.setRecoverPasswordAnswer(rs.getString("recover_password_answer"));
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error validating customer login", e);
        } finally {
            DBUtils.closeQuietly(rs, ps);
        }
        return user;
    }
}
