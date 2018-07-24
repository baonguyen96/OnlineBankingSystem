package api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import core.Logger;
import dao.UserDaoImpl;
import domain.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/api/register")
public class RegistrationController extends JsonServletBase<User> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = new Logger(RegistrationController.class);
    private static final String SUCCESS_STATUS = "Success";
    private static final String REGISTRATION_FAILED_STATUS = "The registration failed";
    private static final String REQUIRED_FIELDS_MISSING_STATUS = "Required fields are missing";

    @Override
    protected boolean requireValidSession() {
        LOG.log(Logger.Action.BEGIN);
        LOG.log(Logger.Action.RETURN, "is valid session required");
        return false;
    }

    /**
     * Provides the CREATE (New User Registration) action 
     */
    @Override
    protected User processPost(HttpServletRequest request, HttpServletResponse response, User user) {
        LOG.setScenarioName("New User Registration");
        LOG.log(Logger.Action.BEGIN, "request", "response", "user");
        LOG.info(user.toString());

        if (user.getUsername() == null //
                || user.getPassword() == null //
                || user.getName() == null //
                || user.getRecoverPasswordQuestion() == null //
                || user.getRecoverPasswordAnswer() == null) {

            user.setStatus(REQUIRED_FIELDS_MISSING_STATUS);
        } else {
            User registeredUser = new UserDaoImpl().register(user);
            if (createNewUserSession(request, user)) {
                user = registeredUser;
                user.setStatus(SUCCESS_STATUS);
            } else {
                user.setStatus(REGISTRATION_FAILED_STATUS);
            }
        }
        user.setPassword(null); // because security
        user.setRecoverPasswordAnswer(null); // because security

        LOG.log(Logger.Action.RETURN, "user");
        return user;
    }
}
