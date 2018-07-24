package api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.JsonServletBase;
import core.Logger;
import domain.Status;
import domain.User;

/**
 * Servlet API implementation class StatusController
 */
@WebServlet("/api/status")
public class StatusController extends JsonServletBase<Status> {
    private static final Logger LOG = new Logger(StatusController.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected boolean requireValidSession() {
        LOG.log(Logger.Action.BEGIN);
        LOG.log(Logger.Action.RETURN, "is valid session required");
        return false;
    }

    @Override
    protected Status processGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.setScenarioName("Check Login Status");
        LOG.log(Logger.Action.BEGIN, "request", "response");
        Status status = null;

        User user = getUserFromSession(request);
        if (user != null) {
            status = new Status();
            status.setUsername(user.getUsername());
        }

        LOG.log(Logger.Action.RETURN, "logged-in status");
        return status;
    }
}
