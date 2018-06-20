package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import domain.UserAccount;

public abstract class JsonServletBase<T extends Object> extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(JsonServletBase.class.getName());
    private static final int SESSION_INACTIVE_TIMEOUT = 5 * 60; // 5 minutes * 60 seconds
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public JsonServletBase() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected boolean requireValidSession() {
        return true;
    }

    /**
     * Creates a new user session properly initialized with the expected values.
     * 
     * @param request The current non-null session associated with the user's request
     * @param user A populated non-null and valid user object
     * @return true if the user session was created successfully, otherwise false
     */
    protected boolean createNewUserSession(HttpServletRequest request, UserAccount user) {
        boolean successful = false;
        if (request != null //
                && user != null //
                && user.getUserID() != null //
                && user.getUsername() != null //
                && user.getName() != null) {
            HttpSession session = request.getSession();
            session.setAttribute(SessionConstants.USER, user);
            session.setMaxInactiveInterval(SESSION_INACTIVE_TIMEOUT);
            successful = true;
            LOG.info("Created new User Session for " + user.getUsername() + "; Session ID: " + session.getId());
        }
        return successful;
    }

    protected UserAccount getUserFromSession(HttpServletRequest request) {
        UserAccount user = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object sessUser = session.getAttribute(SessionConstants.USER);
            if (sessUser != null && sessUser instanceof UserAccount) {
                user = (UserAccount) sessUser;
                LOG.info("getUserFromSession(): " + user.toString());
            } else {
                LOG.log(Level.SEVERE, "There's an active session (" + session.getId() + "), but no valid UserAccount object in it");
            }
        } else {
            LOG.info("getUserFromSession(): No active session, returning null");
        }
        return user;
    }

    /**
     * Removes any active session for the current user.
     * 
     * @param request The current non-null HttpServletRequest associated with the user's request
     */
    protected void removeUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
    }

    /**
     * Handles Retrieve by id REST requests.
     * 
     * @param id The id of the object to retrieve
     * @return The object being requested
     * @throws ServletException
     * @throws IOException
     */
    protected abstract T processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException;

    /**
     * Handles Retrieve All REST requests, returning the appropriate objects given the context
     * 
     * @param request The current non-null HttpServletRequest associated with the user's request
     * @param response
     * @return The objects being requested
     * @throws ServletException
     * @throws IOException
     */
    protected abstract Collection<T> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    /**
     * Handles the Create REST requests.
     * @param request The current non-null HttpServletRequest associated with the user's request
     * @param response
     * @param postObject
     * @return The object that was Created
     * @throws ServletException
     * @throws IOException
     */
    protected abstract T processPost(HttpServletRequest request, HttpServletResponse response, T postObject) throws ServletException, IOException;

    /**
     * Handles the Update REST requests.
     * @param request The current non-null HttpServletRequest associated with the user's request
     * @param response
     * @param putObject
     * @return The object that was Updated
     * @throws ServletException
     * @throws IOException
     */
    protected abstract T processPut(HttpServletRequest request, HttpServletResponse response, T putObject) throws ServletException, IOException;

    /**
     * Handles the Delete REST requests.
     * 
     * @param request The current non-null HttpServletRequest associated with the user's request
     * @param response
     * @param deleteObject
     * @throws ServletException
     * @throws IOException
     */
    protected abstract void processDelete(HttpServletRequest request, HttpServletResponse response, T deleteObject) throws ServletException, IOException;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Level.INFO, "doGet(): BEGIN; Path: " + request.getServletPath());

        HttpSession session = request.getSession(false);
        UserAccount user = getUserFromSession(request);
        LOG.info("doGet(): Session ID: " + ((session != null) ? session.getId() : "null") + "; User: " + ((user != null) ? user.getUsername() : "null"));

        if (requireValidSession() && session == null) {
            writeNotAuthorizedErrorResponse(response);
            LOG.log(Level.INFO, "doPost(): END; NOT_AUTHORIZED");
            return;
        }

        try {
            String idString = request.getParameter("id");
            String returnJsonString = null;

            if (idString == null) {
                Collection<T> objectToReturn = processGetAll(request, response);
                returnJsonString = objectsToJson(objectToReturn);
            } else {
                Long id = Long.parseLong(idString.trim());
                T objectToReturn = processGet(request, response, id);
                returnJsonString = objectToJson(objectToReturn);
            }

            LOG.log(Level.INFO, "doGet(): END; " + returnJsonString);
            writeJsonResponse(response, returnJsonString);
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, "doGet(): " + e.getMessage());
            writeClientErrorResponse(response);
            return;
        } catch (ServletException | IOException e) {
            LOG.log(Level.SEVERE, "doGet(): " + e.getMessage());
            writeServerErrorResponse(response);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Level.INFO, "doPost(): BEGIN");

        HttpSession session = request.getSession(false);
        if (requireValidSession() && session == null) {
            writeNotAuthorizedErrorResponse(response);
            LOG.log(Level.INFO, "doPost(): END; NOT_AUTHORIZED");
            return;
        }

        T inputObject = null;
        try {
            String requestInputString = readRequestAsString(request);
            LOG.log(Level.INFO, "doPost(): INPUT; " + requestInputString);
            inputObject = jsonToObject(requestInputString);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "doGet(): " + e.getMessage());
            writeClientErrorResponse(response);
            return;
        }

        try {
            T objectToReturn = processPost(request, response, inputObject);
            String returnJsonString = objectToJson(objectToReturn);

            LOG.log(Level.INFO, "doPost(): END; " + returnJsonString);
            writeJsonResponse(response, returnJsonString);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "doGet(): " + e.getMessage());
            writeServerErrorResponse(response);
            return;
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Level.INFO, "doPost(): BEGIN");

        HttpSession session = request.getSession(false);
        if (requireValidSession() && session == null) {
            writeNotAuthorizedErrorResponse(response);
            LOG.log(Level.INFO, "doPost(): END; NOT_AUTHORIZED");
            return;
        }

        T inputObject = null;
        try {
            String requestInputString = readRequestAsString(request);
            LOG.log(Level.INFO, "doPost(): INPUT; " + requestInputString);
            inputObject = jsonToObject(requestInputString);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "doGet(): " + e.getMessage());
            writeClientErrorResponse(response);
            return;
        }

        try {
            T objectToReturn = processPut(request, response, inputObject);
            String returnJsonString = objectToJson(objectToReturn);

            LOG.log(Level.INFO, "doPost(): END; " + returnJsonString);
            writeJsonResponse(response, returnJsonString);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "doGet(): " + e.getMessage());
            writeServerErrorResponse(response);
            return;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Level.INFO, "doDelete(): BEGIN");

        HttpSession session = request.getSession(false);
        if (requireValidSession() && session == null) {
            writeNotAuthorizedErrorResponse(response);
            LOG.log(Level.INFO, "doPost(): END; NOT_AUTHORIZED");
            return;
        }

        T inputObject = null;
        try {
            String requestInputString = readRequestAsString(request);
            LOG.log(Level.INFO, "doDelete(): INPUT; " + requestInputString);
            inputObject = jsonToObject(requestInputString);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "doDelete(): " + e.getMessage());
            writeClientErrorResponse(response);
            return;
        }

        try {
            processDelete(request, response, inputObject);
            LOG.log(Level.INFO, "doDelete(): END");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "doDelete(): " + e.getMessage());
            writeServerErrorResponse(response);
            return;
        }
    }

    /**
     * Sets the response status code to 204 - Success but no content
     * 
     * @param response The current non-null HttpServletResponse associated with the user's request
     */
    protected void writeDeleteSuccessfulResponse(HttpServletResponse response) {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void writeClientErrorResponse(HttpServletResponse response) {
        PrintWriter out;
        try {
            out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print("{ \"errorMessage\": \"Sorry, but there was something wrong with your request :(\"}");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeServerErrorResponse(HttpServletResponse response) {
        PrintWriter out;
        try {
            out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print("{ \"errorMessage\": \"Sorry, but someting broke.  Please try again later :(\"}");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeNotAuthorizedErrorResponse(HttpServletResponse response) {
        PrintWriter out;
        try {
            out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print("{ \"errorMessage\": \"I'm sorry Dave, but I cannot allow you to do that :(\"}");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeJsonResponse(HttpServletResponse response, String returnJsonString) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(returnJsonString);
        out.flush();
    }

    private String readRequestAsString(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    private T jsonToObject(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectReader objectReader = objectMapper.readerFor(type);

        return objectReader.readValue(json);
    }

    private String objectToJson(T objectToConvert) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(objectToConvert);
    }

    private String objectsToJson(Collection<T> objectsToConvert) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(objectsToConvert);
    }
}
