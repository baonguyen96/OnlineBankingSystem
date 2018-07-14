package api;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import core.JsonServletBase;
import domain.User;

/**
 * Servlet API implementation class UserController
 * 
 * The CREATE (New User Registration) action is handled by the RegistrationController 
 * 
 */
@WebServlet("/api/users/{userId}")
public class UserController extends JsonServletBase<User> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    private static final String SUCCESS_STATUS = "Success";

    public UserController() {
    }

    @Override
    protected boolean requireValidSession() {
        return true;
    }

}
