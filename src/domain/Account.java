package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import core.DbBaseObject;

public class Account extends DbBaseObject {

    @JsonIgnore
    private User user;

    private String name;

    @JsonProperty(access = Access.READ_ONLY)
    private BigDecimal balance;

    @JsonProperty(access = Access.READ_ONLY)
    private List<Transaction> transactions = new ArrayList<Transaction>();

    /**
     * Used for returning API call Status Information
     */
    private String status;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        return "Account [user=" + ((user != null) ? user.getUsername() : "") + ", name=" + name + ", balance=" + balance + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + ", transactions=" + transactions + ", status=" + status + "]";
    }

    @Override
    @JsonGetter(value = "id")
    @JsonProperty(access = Access.READ_ONLY)
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Account other = (Account) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (user == null) {
            if (other.user != null) return false;
        } else if (!user.equals(other.user)) return false;
        return true;
    }

}
