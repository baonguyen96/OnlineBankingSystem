package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.UserDao;
import db.DbManager;
import entity.reg.UserAccount;
import entity.staging.Login;
import util.Utilities;


public class UserAccountImpl implements UserDao {

    private static Connection conn;
    private static PreparedStatement ps;
    private DbManager db = new DbManager();


    @Override
    public int register(UserAccount user) {
	int status = 0;

	try {
	    // update the password with its hashed version
	    user.setPassword(Utilities.hash(user.getPassword()));

	    conn = db.getConnection();
	    ps = conn.prepareStatement(
		    "INSERT INTO user_accounts "
		    + "(username, password, full_name, recover_password_question, recover_password_answer, balance) "
		    + "value ('?', '?', '?', '', '', 0);");
	    ps.setString(1, user.getUsername());
	    ps.setString(2, user.getPassword());
	    ps.setString(3, user.getName());
	    ps.setString(4, user.getRecoverPasswordQuestion());
	    ps.setString(5, user.getRecoverPasswordAnswer());
	    status = ps.executeUpdate();
	    conn.close();
	}
	catch (Exception e) {
	    System.out.println(e);
	}

	return status;
    }


    @Override
    public UserAccount validateCustomer(Login login) {
	UserAccount user = new UserAccount();

	try {
	    
	    conn = db.getConnection();
	    ps = conn.prepareStatement("SELECT * FROM user_accounts WHERE username = '?' AND password = '?';");
	    ps.setString(1, login.getUsername());
	    ps.setString(1, Utilities.hash(login.getPassword()));
	    
	    ResultSet rs = ps.executeQuery();
	    while (rs.next()) {
		user.setUserID(rs.getInt("user_id"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setName(rs.getString("full_name"));
		user.setRecoverPasswordQuestion(rs.getString("recover_password_question"));
		user.setRecoverPasswordAnswer(rs.getString("recover_password_answer"));
		user.setBalance(rs.getDouble("balance"));
	    }
	    conn.close();
	}
	catch (Exception e) {
	    System.out.println(e);
	}
	return user;
    }

}
