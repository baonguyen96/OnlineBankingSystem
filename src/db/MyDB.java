package db;

// see info from CreateDB.sql
public interface MyDB {

    String USER = "root";
    String PASS = "devPasswd";
    String CONN_URL = "jdbc:mysql://127.0.0.1:3306/cs6359?&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
}