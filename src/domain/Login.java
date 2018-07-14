package domain;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * 
 * @author mehra
 * This is the Customer before the validation.
 */
public class Login {
    private String username = null;
    private String password = null;

    /**
     * Used for returning API call Status Information
     */
    private String status = null;

    public Login() {
        // Default constructor necessary for Jackson JSON deserialization
    }

    public Login(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Login [username=" + username + ", password=" + password + ", status=" + status + "]";
    }

    @JsonGetter(value = "id")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }
}
