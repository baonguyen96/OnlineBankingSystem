package uat;

import java.sql.SQLException;

public class UserAcceptanceTest {

    public static void main(String[] args) {
	
	try {
	    new BankChainTest().run();
	}
	catch (SQLException e) {
	    e.printStackTrace();
	}
	
    }
    
}
