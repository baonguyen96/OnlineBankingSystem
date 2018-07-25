package domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import core.DbBaseObject;
import core.Logger;

public class Transaction extends DbBaseObject {
    private static final Logger LOG = new Logger(Transaction.class);

    protected TransactionType type;

    @JsonProperty(access = Access.WRITE_ONLY)
    protected Account account;
    protected BigDecimal amount;

    @JsonProperty(access = Access.WRITE_ONLY)
    protected Account transferFromAccount;
    protected Account transferToAccount;

    /**
     * Used for returning API call Status Information
     */
    protected String status;

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
        if (TransactionType.TransferOut == type && getAccount() != null) setTransferFromAccount(getAccount());
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        if (TransactionType.TransferOut == getType()) setTransferFromAccount(account);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getTransferFromAccount() {
        return transferFromAccount;
    }

    public void setTransferFromAccount(Account transferFromAccount) {
        this.transferFromAccount = transferFromAccount;
    }

    public Account getTransferToAccount() {
        return transferToAccount;
    }

    public void setTransferToAccount(Account transferToAccount) {
        this.transferToAccount = transferToAccount;
    }

    @JsonProperty(access = Access.READ_ONLY)
    public int getId() {
        return hashCode();
    }

    @JsonIgnore
    public boolean isValid() {
        LOG.log(Logger.Action.BEGIN);
        try {
            if (getAmount() == null) return false;
            if (getType() == null) return false;
            if (getAccount() == null) return false;

            if (getAmount().doubleValue() == 0.0) {
                return false;
            } else if (getAmount().doubleValue() < 0.0) {

                if (TransactionType.Withdraw == getType()) return true;

                if (TransactionType.TransferOut == getType()) {
                    if (getTransferFromAccount() == null) return false;
                    if (getTransferToAccount() == null) return false;
                    if (getAccount().equals(getTransferFromAccount())) return true;
                }
            } else if (getAmount().doubleValue() > 0.0) {
                if (TransactionType.Deposit == getType()) return true;
                if (TransactionType.TransferIn == getType()) return true;
            }
            return false;
        } finally {
            LOG.log(Logger.Action.RETURN, "boolean");
        }
    }

    @Override
    public String toString() {
        return "Transaction [type=" + type + ", account=" + ((account != null) ? account.getName() : "") + ", amount=" + amount + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + ", status=" + status + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((account == null) ? 0 : account.hashCode());
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Transaction other = (Transaction) obj;
        if (account == null) {
            if (other.account != null) return false;
        } else if (!account.equals(other.account)) return false;
        if (createdOn == null) {
            if (other.createdOn != null) return false;
        } else if (!createdOn.equals(other.createdOn)) return false;
        if (type != other.type) return false;
        return true;
    }

    public Transaction cloneForTransfer() {
        LOG.log(Logger.Action.BEGIN);
        if (TransactionType.TransferOut != getType()) return null;
        if (!isValid()) return null;

        Transaction clone = new Transaction();
        clone.type = TransactionType.TransferIn;
        clone.amount = getAmount().negate();
        clone.account = getTransferToAccount();
        LOG.log(Logger.Action.RETURN, "transfer to transaction");
        return clone;
    }
}
