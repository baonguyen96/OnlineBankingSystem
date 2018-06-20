package domain;

/**
 * This class is used to satisfy the API contract for checking the status of a user's session
 * 
 * @author rwiles
 *
 */
public class Status {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
