package api;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import domain.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/api/user")
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

    @Override
    protected User processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException {
        return null;
    }

    @Override
    protected Collection<User> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    /**
     * The CREATE (New User Registration) action is handled by the RegistrationController 
     */
    @Override
    protected User processPost(HttpServletRequest request, HttpServletResponse response, User user) throws ServletException, IOException {
        return null;
    }

    @Override
    protected User processPut(HttpServletRequest request, HttpServletResponse response, User putObject) throws ServletException, IOException {
        return null;
    }

    @Override
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, User deleteObject) throws ServletException, IOException {
    }

}
