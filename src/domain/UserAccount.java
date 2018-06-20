package domain;

import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

public class UserAccount {

    private Long userID;
    private String username;
    private String password;
    private String name;
    private String recoverPasswordQuestion;
    private String recoverPasswordAnswer;
    private double balance;
    private LinkedList<Transaction> transactions;
    private String status = null;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = StringUtils.trimToNull(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = StringUtils.trimToNull(password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trimToNull(name);
    }

    public String getRecoverPasswordQuestion() {
        return recoverPasswordQuestion;
    }

    public void setRecoverPasswordQuestion(String recoverPasswordQuestion) {
        this.recoverPasswordQuestion = StringUtils.trimToNull(recoverPasswordQuestion);
    }

    public String getRecoverPasswordAnswer() {
        return recoverPasswordAnswer;
    }

    public void setRecoverPasswordAnswer(String recoverPasswordAnswer) {
        this.recoverPasswordAnswer = StringUtils.trimToNull(recoverPasswordAnswer);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserAccount [userID=" + userID + ", username=" + username + ", password=" + password + ", name=" + name + ", recoverPasswordQuestion=" + recoverPasswordQuestion + ", recoverPasswordAnswer=" + recoverPasswordAnswer + ", balance="
                + balance + ", status=" + status + "]";
    }

}
