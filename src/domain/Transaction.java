package domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import core.DbBaseObject;

public class Transaction extends DbBaseObject {

    protected TransactionType type;
    protected Long accountId;
    protected BigDecimal amount;

    @JsonProperty(access = Access.READ_ONLY)
    private Date createdOn;

    @JsonProperty(access = Access.READ_ONLY)
    private Date updatedOn;

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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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

    @Override
    public String toString() {
        return "Transaction [id=" + id + ", type=" + type + ", accountId=" + accountId + ", amount=" + amount + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + ", status=" + status + "]";
    }

}
