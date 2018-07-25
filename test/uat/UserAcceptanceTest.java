package uat;

import uat.BankChainTest.Browser;

public class UserAcceptanceTest {

    public static void main(String[] args) {
	
	try {
	    new BankChainTest(Browser.FireFox).run();
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	
    }
    
}
