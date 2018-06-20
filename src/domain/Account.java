package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import core.DbBaseObject;

public class Account extends DbBaseObject {

    @JsonProperty(access = Access.READ_ONLY)
    private Long userId;

    private String name;

    @JsonProperty(access = Access.READ_ONLY)
    private BigDecimal balance;

    @JsonProperty(access = Access.READ_ONLY)
    private Date createdOn;

    @JsonProperty(access = Access.READ_ONLY)
    private Date updatedOn;

    @JsonProperty(access = Access.READ_ONLY)
    private List<Transaction> transactions = new ArrayList<Transaction>();

    /**
     * Used for returning API call Status Information
     */
    private String status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
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
        return "Account [userId=" + userId + ", name=" + name + ", balance=" + balance + ", updatedOn=" + updatedOn + ", transactions=" + transactions + ", status=" + status + "]";
    }

}
