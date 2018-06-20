package api;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import domain.Status;
import domain.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/api/status")
public class StatusController extends JsonServletBase<Status> {
    private static final long serialVersionUID = 1L;

    public StatusController() {
    }

    @Override
    protected boolean requireValidSession() {
        return false;
    }

    @Override
    protected Status processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException {
        Status status = null;

        User user = getUserFromSession(request);
        if (user != null) {
            status = new Status();
            status.setUserName(user.getUsername());
        }

        return status;
    }

    @Override
    protected Collection<Status> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    @Override
    protected Status processPost(HttpServletRequest request, HttpServletResponse response, Status loginRequest) throws ServletException, IOException {
        return null;
    }

    @Override
    protected Status processPut(HttpServletRequest request, HttpServletResponse response, Status putObject) throws ServletException, IOException {
        return null;
    }

    @Override
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, Status deleteObject) throws ServletException, IOException {
    }

}
