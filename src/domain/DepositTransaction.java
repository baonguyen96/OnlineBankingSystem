package domain;

public class DepositTransaction extends Transaction {

    public DepositTransaction() {
        super();
        type = TransactionType.Deposit;
    }

}
