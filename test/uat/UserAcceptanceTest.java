package uat;

public class UserAcceptanceTest {

    public static void main(String[] args) {
	
	try {
	    new BankChainTest().run();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	
    }
    
}
