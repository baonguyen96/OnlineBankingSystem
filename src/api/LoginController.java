package api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import core.Logger;
import dao.UserDaoImpl;
import domain.Login;
import domain.User;

/**
 * Servlet API implementation class LoginController
 */
@WebServlet("/api/login")
public class LoginController extends JsonServletBase<Login> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = new Logger(LoginController.class);
    private static final String SUCCESS_STATUS = "Success";
    private static final String INVALID_STATUS = "Unknown Username or Password";

    @Override
    protected boolean requireValidSession() {
        LOG.log(Logger.Action.BEGIN);
        LOG.log(Logger.Action.RETURN, "is valid session required");
        return false;
    }

    /**
     * Processes a User login request and if they successfully authenticate creates their session
     */
    @Override
    protected Login processPost(HttpServletRequest request, HttpServletResponse response, Login loginRequest) {
        LOG.setScenarioName("User Login");
        LOG.log(Logger.Action.BEGIN, "request", "response", "login request");

        loginRequest.setStatus(INVALID_STATUS);

        LOG.log(Logger.Action.ALT_START, "username != null && password != null");
        if (loginRequest.getUsername() != null && loginRequest.getPassword() != null) {
            User user = new UserDaoImpl().validateUser(loginRequest);
            LOG.log(Logger.Action.ALT_START, "session created");
            if (createNewUserSession(request, user)) {
                loginRequest.setStatus(SUCCESS_STATUS);
            }
            LOG.log(Logger.Action.ALT_END);
        }
        LOG.log(Logger.Action.ALT_END);

        loginRequest.setPassword(null); // because security

        LOG.log(Logger.Action.RETURN, "login status");
        return loginRequest;
    }

    /**
     * Processes a User logout request.  For security reasons, this always returns a 204 No-Content response code
     */
    @Override
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, Login deleteObject) {
        LOG.setScenarioName("Logout");
        LOG.log(Logger.Action.BEGIN, "request", "response", "logout request");
        removeUserSession(request);
        writeDeleteSuccessfulResponse(response);
        LOG.log(Logger.Action.RETURN, "204 No-Content response");
    }

}
