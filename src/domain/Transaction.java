package domain;

import core.DbBaseObject;

public class Transaction extends DbBaseObject {

    protected TransactionType type;
    protected double amount;

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
