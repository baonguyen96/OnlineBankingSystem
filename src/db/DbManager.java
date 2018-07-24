package db;

import java.sql.Connection;
import java.sql.DriverManager;

import core.Logger;

/**
 * 
 * @author mehra getConnection method help us to connect to the appropriate
 *         database. In this project we only have one database. Data comes from
 *         MyDB interface.
 */
public class DbManager implements MyDB {
    private static final Logger LOG = new Logger(DbManager.class);

    public Connection getConnection() {
        LOG.log(Logger.Action.BEGIN);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection myConnection = DriverManager.getConnection(CONN_URL, USER, PASS);
            return myConnection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            LOG.log(Logger.Action.RETURN, "dbConnection");
        }
    }
}
