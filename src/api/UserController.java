package api;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import core.JsonServletBase;
import core.SessionConstants;
import dao.UserDaoImpl;
import dao.UserDao;
import domain.UserAccount;

/**
 * Servlet implementation class Login
 */
@WebServlet("/api/user")
public class UserController extends JsonServletBase<UserAccount> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    private static final String SUCCESS_STATUS = "Success";
    private static final String REGISTRATION_FAILED_STATUS = "The registration failed";
    private static final String REQUIRED_FIELDS_MISSING_STATUS = "Required fields are missing";

    public UserController() {
    }

    @Override
    protected boolean requireValidSession() {
        return false;
    }

    @Override
    protected UserAccount processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException {
        return null;
    }

    @Override
    protected Collection<UserAccount> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    /**
     * Provides the CREATE action 
     */
    @Override
    protected UserAccount processPost(HttpServletRequest request, HttpServletResponse response, UserAccount user) throws ServletException, IOException {
        LOG.log(Level.INFO, user.toString());

        if (user.getUsername() == null //
                || user.getPassword() == null //
                || user.getName() == null //
                || user.getRecoverPasswordQuestion() == null //
                || user.getRecoverPasswordAnswer() == null //
        ) {
            user.setStatus(REQUIRED_FIELDS_MISSING_STATUS);
        } else {
            UserDao userDao = new UserDaoImpl();
            user.setName(request.getParameter("name"));
            user.setUsername(request.getParameter("username"));
            user.setPassword(request.getParameter("password"));
            user.setRecoverPasswordQuestion(request.getParameter("recover-password-question"));
            user.setRecoverPasswordAnswer(request.getParameter("recover-password-answer"));
            UserAccount registeredUser = userDao.register(user);
            if (registeredUser != null) {
                user = registeredUser;

                HttpSession session = request.getSession();
                session.setAttribute(SessionConstants.USER, user);

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
    protected UserAccount processPut(HttpServletRequest request, HttpServletResponse response, UserAccount putObject) throws ServletException, IOException {
        return null;
    }

    @Override
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, UserAccount deleteObject) throws ServletException, IOException {
    }

}
