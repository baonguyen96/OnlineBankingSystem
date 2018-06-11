package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.CustomerDao;
import db.DbManager;
import entity.reg.Customer;
import entity.staging.Login;
import util.Utilities;


public class CustomerDaoImpl implements CustomerDao {

    private static Connection conn;
    private static PreparedStatement ps;
    private DbManager db = new DbManager();


    @Override
    public int register(Customer c) {
	int status = 0;

	try {
	    // update the password with its hashed version
	    c.setPassword(Utilities.hash(c.getPassword()));

	    String query = String.format(
		    "INSERT INTO users (username, password, full_name, recover_password_question, recover_password_answer) "
			    + "value ('%s', '%s', '%s', '%s', '%s');",
		    c.getUsername(), c.getPassword(), c.getName(), c.getRecoverPasswordQuestion(),
		    c.getRecoverPasswordAnswer());

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

	try {

	    String query = String.format("SELECT * FROM users WHERE username = '%s' AND password = '%s';",
		    login.getUsername(), Utilities.hash(login.getPassword()));

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
