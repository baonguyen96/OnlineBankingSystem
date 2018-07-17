package domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import core.DbBaseObject;

public class Transaction extends DbBaseObject {

    protected TransactionType type;

    @JsonProperty(access = Access.WRITE_ONLY)
    protected Account account;
    protected BigDecimal amount;

    /**
     * Used for returning API call Status Information
     */
    protected String status;

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    @JsonProperty(access = Access.READ_ONLY)
    public int getId() {
        return hashCode();
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

}
