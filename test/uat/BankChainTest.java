package uat;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import db.DbManager;


class BankChainTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;


    public BankChainTest() throws SQLException {
	setupSelenium();
	clearDatabase();
    }


    private void setupSelenium() {
	System.setProperty("webdriver.chrome.driver", "assets/driver/chromedriver.exe");
	driver = new ChromeDriver();
	baseUrl = "http://localhost:8080/OnlineBankingSystem/index.html";
	driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
	wait = new WebDriverWait(driver, 30);
    }


    private void clearDatabase() throws SQLException {
	DbManager dbManager = new DbManager();
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


    public void run() {
	verifyHomePage();
	testInvalidLogIn();
	testInvalidRegister();
	testValidRegister();
	testValidLogin();

	quit();
    }


    public void verifyHomePage() {
	try {
	    driver.get(baseUrl);
	    pause(2);
	    assertEquals("BankChain", driver.findElement(By.linkText("BankChain")).getText());
//	    assertEquals("Home", driver.findElement(By.id("menuHome")).getText());
//	    assertEquals("Services", driver.findElement(By.id("menuServices")).getText());
//	    assertEquals("Products", driver.findElement(By.id("menuProducts")).getText());
//	    assertEquals("Welcome to BankChain� Bank", driver.findElement(By.cssSelector("h1")).getText());
	    assertEquals("Sign-Up", driver.findElement(By.id("registerButton")).getText());
	    assertEquals("Login", driver.findElement(By.xpath("(//button[@type='button'])[4]")).getText());

	    log("Verify Home Page: Pass");
	}
	catch (Throwable e) {
	    log("Verify Home Page: Fail");
	    logErrorMessage(e);
	}
	finally {
	    pause(2);
	}
    }


    public void testInvalidLogIn() {
	try {
	    driver.get(baseUrl);
	    pause(2);
	    driver.findElement(By.xpath("(//button[@type='button'])[4]")).click();
	    pause(2);
	    driver.findElement(By.id("loginUsername")).click();
	    pause(2);
	    assertEquals("Please enter your login credentials",
		    driver.findElement(By.xpath("//div[@id='loginScreen']/div/div[2]/h3")).getText());
	    assertEquals("Username",
		    driver.findElement(By.xpath("//div[@id='loginScreen']/div[4]/div[2]/form/div/label")).getText());
	    assertEquals("Password",
		    driver.findElement(By.xpath("//div[@id='loginScreen']/div[4]/div[2]/form/div[2]/label")).getText());
	    driver.findElement(By.id("loginUsername")).click();
	    driver.findElement(By.id("loginUsername")).clear();
	    driver.findElement(By.id("loginUsername")).sendKeys("nonexist");
	    driver.findElement(By.id("loginPassword")).click();
	    driver.findElement(By.id("loginPassword")).clear();
	    driver.findElement(By.id("loginPassword")).sendKeys("nonexist");
	    driver.findElement(By.id("loginSubmitButton")).click();
	    assertEquals("Unknown Username or Password",
		    driver.findElement(By.xpath("//div[@id='loginAlert']/span")).getText());

	    driver.findElement(By.id("menuHome")).click();

	    log("Test Invalid Login: Pass");
	}
	catch (Throwable e) {
	    log("Test Invalid Login: Fail");
	    logErrorMessage(e);
	}
	finally {
	    pause(2);
	}

    }


    private void testInvalidRegister() {
	try {
	    driver.get(baseUrl);
	    pause(2);
	    driver.findElement(By.id("registerButton")).click();
	    pause(2);

	    assertEquals("Welcome to BankChain",
		    driver.findElement(By.xpath("//div[@id='registerScreen']/div/div[2]/h3")).getText());
	    assertEquals("Please fill out our registration form",
		    driver.findElement(By.xpath("//div[@id='registerScreen']/div[2]/div[2]/h4")).getText());
	    assertEquals("Username",
		    driver.findElement(By.xpath("//div[@id='registerScreen']/div[5]/div[2]/form/div/label")).getText());
	    assertEquals("Password", driver
		    .findElement(By.xpath("//div[@id='registerScreen']/div[5]/div[2]/form/div[2]/label")).getText());
	    assertEquals("Full Name", driver
		    .findElement(By.xpath("//div[@id='registerScreen']/div[5]/div[2]/form/div[3]/label")).getText());
	    assertEquals("Password Recovery Question", driver
		    .findElement(By.xpath("//div[@id='registerScreen']/div[5]/div[2]/form/div[4]/label")).getText());
	    assertEquals("Password Recovery Answer", driver
		    .findElement(By.xpath("//div[@id='registerScreen']/div[5]/div[2]/form/div[5]/label")).getText());
	    driver.findElement(By.id("registerSubmitButton")).click();
	    assertEquals("Required fields are missing",
		    driver.findElement(By.xpath("//div[@id='registerAlert']/span")).getText());

	    log("Test Invalid Register: Pass");
	}
	catch (Throwable e) {
	    log("Test Invalid Register: Fail");
	    logErrorMessage(e);
	}
	finally {
	    pause(2);
	}
    }


    private void testValidRegister() {
	try {
	    driver.findElement(By.id("registerUsername")).click();
	    driver.findElement(By.id("registerUsername")).clear();
	    driver.findElement(By.id("registerUsername")).sendKeys("userone");
	    driver.findElement(By.id("registerPassword")).clear();
	    driver.findElement(By.id("registerPassword")).sendKeys("password1");
	    driver.findElement(By.id("registerFullName")).click();
	    driver.findElement(By.id("registerFullName")).clear();
	    driver.findElement(By.id("registerFullName")).sendKeys("User One");
	    driver.findElement(By.id("registerRecoverQuestion")).click();
	    driver.findElement(By.id("registerRecoverQuestion")).clear();
	    driver.findElement(By.id("registerRecoverQuestion")).sendKeys("Question1");
	    driver.findElement(By.id("registerRecoverAnswer")).click();
	    driver.findElement(By.id("registerRecoverAnswer")).clear();
	    driver.findElement(By.id("registerRecoverAnswer")).sendKeys("Answer1");
	    driver.findElement(By.id("registerSubmitButton")).click();
	    pause(2);
	    assertEquals("Accounts Home",
		    driver.findElement(By.xpath("//div[@id='accountsHomeScreen']/div/div[2]/h3")).getText());
	    assertEquals("Create New Account", driver.findElement(By.id("createNewAccountButton")).getText());
	    driver.findElement(By.linkText("My Profile")).click();
	    pause(2);
	    assertEquals("Update Profile", driver.findElement(By.linkText("Update Profile")).getText());
	    assertEquals("Logout", driver.findElement(By.id("logoutButton")).getText());
	    driver.findElement(By.id("logoutButton")).click();
	    pause(2);
	    assertEquals("Sign-Up", driver.findElement(By.id("registerButton")).getText());
	    assertEquals("Login", driver.findElement(By.xpath("(//button[@type='button'])[4]")).getText());

	    log("Test Valid Register: Pass");
	}
	catch (Throwable e) {
	    log("Test Valid Register: Fail");
	    logErrorMessage(e);
	}
	finally {
	    pause(2);
	}
    }


    private void testValidLogin() {

	try {
	    driver.get(baseUrl);
	    pause(2);
	    driver.findElement(By.xpath("(//button[@type='button'])[4]")).click();
	    pause(2);
	    assertEquals("Please enter your login credentials",
		    driver.findElement(By.xpath("//div[@id='loginScreen']/div/div[2]/h3")).getText());
	    driver.findElement(By.id("loginUsername")).click();
	    driver.findElement(By.id("loginUsername")).clear();
	    driver.findElement(By.id("loginUsername")).sendKeys("userone");
	    driver.findElement(By.id("loginPassword")).click();
	    driver.findElement(By.id("loginPassword")).clear();
	    driver.findElement(By.id("loginPassword")).sendKeys("password1");
	    driver.findElement(By.id("loginSubmitButton")).click();
	    pause(2);
	    assertEquals("Accounts Home",
		    driver.findElement(By.xpath("//div[@id='accountsHomeScreen']/div/div[2]/h3")).getText());
	    assertEquals("Name", driver.findElement(By.xpath("//ul[@id='accountsList']/div/div/strong")).getText());
	    assertEquals("Last Updated",
		    driver.findElement(By.xpath("//ul[@id='accountsList']/div/div[2]/strong")).getText());
	    assertEquals("Balance",
		    driver.findElement(By.xpath("//ul[@id='accountsList']/div/div[3]/strong")).getText());

	    log("Test Valid Login: Pass");
	}
	catch (Throwable e) {
	    log("Test Valid Login: Fail");
	    logErrorMessage(e);
	}
	finally {
	    pause(2);
	}
    }


    private void templateMethod() {
	try {
	    log("Test Invalid Login: Pass");
	}
	catch (Throwable e) {
	    log("Test Invalid Login: Fail");
	    logErrorMessage(e);
	}
	finally {
	    pause(2);
	}
    }


    private void log(String message) {
	System.out.println(message);
    }

    
    private void logErrorMessage(Throwable t) {
	System.out.printf("> Error: %s\n", t.getStackTrace()[0]);
    }
    

    private void pause(int seconds) {
	try {
	    Thread.sleep(seconds * 1000);
	}
	catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }


    private boolean isElementPresent(By by) {
	try {
	    driver.findElement(by);
	    return true;
	}
	catch (NoSuchElementException e) {
	    return false;
	}
    }


    private void quit() {
	driver.quit();
    }

}