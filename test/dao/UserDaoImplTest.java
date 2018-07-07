package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.DbManager;
import domain.Login;
import domain.User;


public class UserDaoImplTest {

    static DbManager dbManager;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	dbManager = new DbManager();
	Connection conn = dbManager.getConnection();
	Statement statement = null;

	statement = conn.createStatement();
	statement.addBatch("SET FOREIGN_KEY_CHECKS = 0;");
	statement.addBatch("TRUNCATE table user;");
	statement.addBatch("TRUNCATE table account;");
	statement.addBatch("TRUNCATE table transaction;");
	statement.addBatch("SET FOREIGN_KEY_CHECKS = 1;");
	statement.executeBatch();
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }


    @Before
    public void setUp() throws Exception {
    }


    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testLoginNoneExist() {
	Login noneExist = new Login();
	noneExist.setUsername("noneExist");
	noneExist.setPassword("noneExist");
	UserDao userDao = new UserDaoImpl();
	User user = userDao.validateUser(noneExist);
	assertNull(user);
    }


    @Test
    public void testLoginExist() {
	// register
	User rawUser = new User();
	rawUser.setName("FirstLogin LastLogin");
	rawUser.setUsername("usernameLogin");
	rawUser.setPassword("passwordLogin");
	rawUser.setRecoverPasswordQuestion("questionLogin");
	rawUser.setRecoverPasswordAnswer("answerLogin");
	UserDao userDao = new UserDaoImpl();
	userDao.register(rawUser);
	
	// verify login
	Login login = new Login();
	login.setUsername("usernameLogin");
	login.setPassword("passwordLogin");
	User logedInUser = userDao.validateUser(login);
	assertNotNull(logedInUser);
	assertEquals("usernameLogin", logedInUser.getUsername());
	assertEquals("FirstLogin LastLogin", logedInUser.getName());
	assertEquals("questionLogin", logedInUser.getRecoverPasswordQuestion());
    }


    @Test
    public void testRegisterUser() {
	User rawUser = new User();
	rawUser.setName("FirstRegister LastRegister");
	rawUser.setUsername("usernameRegister");
	rawUser.setPassword("passwordRegister");
	rawUser.setRecoverPasswordQuestion("questionRegister");
	rawUser.setRecoverPasswordAnswer("answerRegister");

	UserDao userDao = new UserDaoImpl();
	User registeredUser = userDao.register(rawUser);

	assertEquals("FirstRegister LastRegister", registeredUser.getName());
	assertEquals("usernameRegister", registeredUser.getUsername());
	assertEquals("questionRegister", registeredUser.getRecoverPasswordQuestion());
	assertNull(registeredUser.getPassword());
	assertNull(registeredUser.getRecoverPasswordAnswer());
    }

}
