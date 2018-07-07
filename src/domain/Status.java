package domain;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * This class is used to satisfy the API contract for checking the status of a user's session
 * 
 * @author rwiles
 *
 */
public class Status {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonGetter(value = "id")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

}
