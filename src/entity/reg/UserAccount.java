package entity.reg;

import java.util.LinkedList;


public class UserAccount {

    private int userID;
    private String username;
    private String password;
    private String name;
    private String recoverPasswordQuestion;
    private String recoverPasswordAnswer;
    private double balance;
    private LinkedList<Transaction> transactions;


    public int getUserID() {
	return userID;
    }


    public void setUserID(int userID) {
	this.userID = userID;
    }


    public String getUsername() {
	return username;
    }


    public void setUsername(String username) {
	this.username = username;
    }


    public String getPassword() {
	return password;
    }


    public void setPassword(String password) {
	this.password = password;
    }


    public String getName() {
	return name;
    }


    public void setName(String name) {
	this.name = name;
    }


    public String getRecoverPasswordQuestion() {
	return recoverPasswordQuestion;
    }


    public void setRecoverPasswordQuestion(String recoverPasswordQuestion) {
	this.recoverPasswordQuestion = recoverPasswordQuestion;
    }


    public String getRecoverPasswordAnswer() {
	return recoverPasswordAnswer;
    }


    public void setRecoverPasswordAnswer(String recoverPasswordAnswer) {
	this.recoverPasswordAnswer = recoverPasswordAnswer;
    }


    public double getBalance() {
	return balance;
    }


    public void setBalance(double balance) {
	this.balance = balance;
    }


    public LinkedList<Transaction> getTransactions() {
	return transactions;
    }


    public void setTransactions(LinkedList<Transaction> transactions) {
	this.transactions = transactions;
    }

}
