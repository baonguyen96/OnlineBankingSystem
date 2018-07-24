package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {
    private static final Logger LOG = new Logger(DBUtils.class);

    /**
     * Selects the max id from the specified table
     * 
     * @param conn
     * @param tableName
     * @return
     */
    public static long getMaxIdFromTable(Connection conn, String tableName) {
        Statement stmt = null;
        ResultSet rs = null;
        long maxId = 0;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select max(id) from " + tableName);

            if (rs.next()) {
                maxId = rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs, stmt);
        }
        return maxId;
    }

    /**
     * Get the ID of the last inserted value
     * 
     * @param conn
     * @return
     * @throws SQLException
     */
    public static Long getLastInsertId(Connection conn) throws SQLException {
        Long id = null;
        String queryStr = "select LAST_INSERT_ID()";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(queryStr);
        if (rs.next()) id = rs.getLong(1);

        closeQuietly(rs, stmt);
        return id;
    }

    /**
     * Helper method that returns a String in the form of
     * "order by COLUMN desc limit 10, 2"
     * 
     * @param startPosition
     * @param maxResult
     * @param orderBy
     * @param asc
     * @return
     */
    public static String getOrderByAndLimit(int startPosition, int maxResult, String orderBy, boolean asc) {
        String query = "";
        if (orderBy != null) {
            query += " order by " + orderBy;
            if (!asc) {
                query += " desc";
            }
        }
        if (maxResult >= 0 && startPosition >= 0) query += " limit " + startPosition + ", " + maxResult;
        return query;
    }

    /**
     * Helper method that closes a ResultSet, Statement, and a
     * Connection without throwing any Exceptions
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void closeQuietly(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {}
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {}
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {}
    }

    /**
     * Helper method that closes a ResultSet, PreparedStatement, and a
     * Connection without throwing any Exceptions
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void closeQuietly(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {}
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {}
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {}
    }

    /**
     * Helper method that closes a Statement, and a Connection without throwing
     * any Exceptions
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void closeQuietly(Statement stmt, Connection conn) {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {}
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {}
    }

    /**
     * Helper method that closes a PreparedStatement, and a Connection without
     * throwing any Exceptions
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void closeQuietly(PreparedStatement stmt, Connection conn) {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {}
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {}
    }

    /**
     * Helper method that closes a ResultSet and a Statement without throwing
     * any Exceptions
     * 
     * @param rs
     * @param stmt
     */
    public static void closeQuietly(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {}
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {}
    }

    /**
     * Helper method that closes a Connection without throwing any Exceptions
     * 
     * @param rs
     * @param stmt
     */
    public static void closeQuietly(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {}
    }

    /**
     * Helper method that closes a ResultSet without throwing any Exceptions
     * 
     * @param rs
     * @param stmt
     */
    public static void closeQuietly(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {}
    }

    /**
     * Helper method that closes a Statement without throwing any Exceptions
     * 
     * @param rs
     * @param stmt
     */
    public static void closeQuietly(Statement stmt) {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {}
    }

}
