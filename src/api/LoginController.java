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
import domain.Login;
import domain.UserAccount;

/**
 * Servlet implementation class Login
 */
@WebServlet("/api/login")
public class LoginController extends JsonServletBase<Login> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());
    private static final String SUCCESS_STATUS = "Success";
    private static final String INVALID_STATUS = "Unknown Username or Password";

    public LoginController() {
    }

    @Override
    protected boolean requireValidSession() {
        return false;
    }

    @Override
    protected Login processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException {
        return null;
    }

    @Override
    protected Collection<Login> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    /**
     * Processes a User login request and if they successfully authenticate creates their session
     */
    @Override
    protected Login processPost(HttpServletRequest request, HttpServletResponse response, Login loginRequest) throws ServletException, IOException {
        LOG.log(Level.INFO, loginRequest.toString());

        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            loginRequest.setStatus(INVALID_STATUS);
        } else {
            UserAccount user = new UserDaoImpl().validateCustomer(loginRequest);
            if (createNewUserSession(request, user)) {
                loginRequest.setStatus(SUCCESS_STATUS);
            } else {
                loginRequest.setStatus(INVALID_STATUS);
            }
        }
        loginRequest.setPassword(null); // because security
        return loginRequest;
    }

    @Override
    protected Login processPut(HttpServletRequest request, HttpServletResponse response, Login putObject) throws ServletException, IOException {
        return null;
    }

    /**
     * Processes a User logout request.  For security reasons, this always returns a 204 No-Content response code
     */
    @Override
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, Login deleteObject) throws ServletException, IOException {
        removeUserSession(request);
        writeDeleteSuccessfulResponse(response);
    }

}
