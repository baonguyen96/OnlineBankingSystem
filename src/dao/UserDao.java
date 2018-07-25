package dao;

import domain.Login;
import domain.User;

public interface UserDao {

    /**
     * Attempts to create a new User, returns the User if it succeeds or null if it fails
     */
    User register(User user);

    /**
     * Validates the Login and returns the User if it succeeds or null on failure.
     */
    User validateUser(Login login);

}