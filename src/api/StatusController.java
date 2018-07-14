package api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import domain.Status;
import domain.User;

/**
 * Servlet API implementation class StatusController
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
    protected Status processGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Status status = null;

        User user = getUserFromSession(request);
        if (user != null) {
            status = new Status();
            status.setUsername(user.getUsername());
        }

        return status;
    }
}
