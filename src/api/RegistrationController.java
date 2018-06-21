package api;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import dao.UserDaoImpl;
import domain.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/api/register")
public class RegistrationController extends JsonServletBase<User> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RegistrationController.class.getName());
    private static final String SUCCESS_STATUS = "Success";
    private static final String REGISTRATION_FAILED_STATUS = "The registration failed";
    private static final String REQUIRED_FIELDS_MISSING_STATUS = "Required fields are missing";

    public RegistrationController() {
    }

    @Override
    protected boolean requireValidSession() {
        return false;
    }

    @Override
    protected User processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException {
        return null;
    }

    @Override
    protected Collection<User> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    /**
     * Provides the CREATE (New User Registration) action 
     */
    @Override
    protected User processPost(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        LOG.log(Level.INFO, user.toString());

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
        return user;
    }

    @Override
    protected User processPut(HttpServletRequest request, HttpServletResponse response, User putObject) throws ServletException, IOException {
        return null;
    }

    @Override
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, User deleteObject) throws ServletException, IOException {
    }

}
