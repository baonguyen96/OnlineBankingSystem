package domain.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import db.DbManager;


public class CustomerDaoImpl implements CustomerDao {

    static Connection conn;
    static PreparedStatement ps;
    DbManager db = new DbManager();


    @Override
    public int register(Customer c) {
	int status = 0;
	String query = String.format(
		"INSERT INTO users (username, password, full_name, recover_password_question, recover_password_answer) "
		+ "value ('%s', '%s', '%s', '%s', '%s');", 
		c.getUsername(), c.getPassword(), c.getName(), c.getRecoverPasswordQuestion(), c.getRecoverPasswordAnswer());
		
	
	try {
	    conn = db.getConnection();
	    ps = conn.prepareStatement(query);
	    status = ps.executeUpdate();
	    conn.close();
	}
	catch (Exception e) {
	    System.out.println(e);
	}

	return status;
    }


    @Override
    public Customer validateCustomer(Login login) {
	Customer c = new Customer();
	String query = String.format(
		"SELECT *\n" + 
		"FROM users\n" + 
		"WHERE username = '%s' AND password = '%s';", 
		login.getUsername(), login.getPassword());
	
	try {
	    conn = db.getConnection();
	    ps = conn.prepareStatement(query);

	    ResultSet rs = ps.executeQuery();
	    while (rs.next()) {
		c.setUsername(rs.getString("username"));
		c.setPassword(rs.getString("password"));
		c.setName(rs.getString("full_name"));
		c.setRecoverPasswordQuestion("recover_password_question");
		c.setRecoverPasswordAnswer("recover_password_answer");
	    }
	    conn.close();
	}
	catch (Exception e) {
	    System.out.println(e);
	}
	return c;
    }

}
