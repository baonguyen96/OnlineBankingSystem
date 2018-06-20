package domain;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
    private List<Account> accounts;

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

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User [iD=" + id + ", username=" + username + ", password=" + password + ", name=" + name + ", recoverPasswordQuestion=" + recoverPasswordQuestion + ", recoverPasswordAnswer=" + recoverPasswordAnswer + ", status=" + status + "]";
    }

}
