package domain;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import core.DbBaseObject;

public class User extends DbBaseObject {

    private String username;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    private String name;
    private String recoverPasswordQuestion;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String recoverPasswordAnswer;

    private Map<Integer, Account> accounts;

    /**
     * Used for returning API call Status Information
     */
    private String status = null;

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

    public void clearAccountsList() {
        if (accounts == null) {
            accounts = new LinkedHashMap<>();
        } else {
            accounts.clear();
        }
    }

    public Collection<Account> getAccounts() {
        if (accounts == null) {
            accounts = new LinkedHashMap<>();
        }
        return accounts.values();
    }

    public void setAccounts(Collection<Account> accounts2) throws Exception {
        if (accounts2 == null) throw new Exception("cannot add a null collection of account");
        if (accounts == null) accounts = new LinkedHashMap<>();
        if (accounts2 != null) {
            accounts2.forEach(account -> accounts.put(account.hashCode(), account));
        }
    }

    public void addAccount(Account account) throws Exception {
        if (account == null) throw new Exception("cannot add a null account to a user");
        if (accounts == null) accounts = new LinkedHashMap<>();
        this.accounts.put(account.hashCode(), account);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", password=" + password + ", name=" + name + ", recoverPasswordQuestion=" + recoverPasswordQuestion + ", recoverPasswordAnswer=" + recoverPasswordAnswer + ", status=" + status + "]";
    }

    @Override
    @JsonGetter(value = "id")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        User other = (User) obj;
        if (username == null) {
            if (other.username != null) return false;
        } else if (!username.equals(other.username)) return false;
        return true;
    }

}
