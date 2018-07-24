package api;

import javax.servlet.annotation.WebServlet;

import core.JsonServletBase;
import core.Logger;
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
    private static final Logger LOG = new Logger(UserController.class);
    private static final String SUCCESS_STATUS = "Success";

    @Override
    protected boolean requireValidSession() {
        LOG.log(Logger.Action.BEGIN);
        LOG.log(Logger.Action.RETURN, "is valid session required");
        return true;
    }

}
