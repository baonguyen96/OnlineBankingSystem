package domain;

public class Transaction {

    protected static class TransactionType {
        public static final String DEPOSIT = "Deposit";
        public static final String WITHDRAW = "Withdraw";
        public static final String TRANSFER_ADD = "Transfer - Send";
        public static final String TRANSFER_RECEIVE = "Transfer - Receive";
    }

    protected int userID;
    protected String transactionType;
    protected double amount;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String type) {
        this.transactionType = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
