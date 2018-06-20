package domain;

public class DepositTransaction extends Transaction {
    
    public DepositTransaction() {
	super();
	transactionType = Transaction.TransactionType.DEPOSIT;
    }

}
