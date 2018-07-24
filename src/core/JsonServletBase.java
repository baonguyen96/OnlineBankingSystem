package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import domain.User;

public abstract class JsonServletBase<T extends Object> extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = new Logger(JsonServletBase.class);

    private static final int SESSION_INACTIVE_TIMEOUT = 5 * 60; // 5 minutes * 60 seconds
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public JsonServletBase() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected boolean requireValidSession() {
        LOG.log(Logger.Action.BEGIN);
        LOG.log(Logger.Action.RETURN, "is valid session required");
        return true;
    }

    /**
     * Creates a new user session properly initialized with the expected values.
     * 
     * @param request The current non-null session associated with the user's request
     * @param user A populated non-null and valid user object
     * @return true if the user session was created successfully, otherwise false
     */
    protected boolean createNewUserSession(HttpServletRequest request, User user) {
        LOG.log(Logger.Action.BEGIN, "request", "user");

        boolean successful = false;
        if (request != null //
                && user != null //
                && user.getUsername() != null //
                && user.getName() != null) {
            HttpSession session = request.getSession();
            session.setAttribute(SessionConstants.USER, user);
            session.setMaxInactiveInterval(SESSION_INACTIVE_TIMEOUT);
            successful = true;
            LOG.info("Created new User Session for " + user.getUsername() + "; Session ID: " + session.getId());
        }
        LOG.log(Logger.Action.RETURN, "user session created");
        return successful;
    }

    protected User getUserFromSession(HttpServletRequest request) {
        LOG.log(Logger.Action.BEGIN, "request");

        User user = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object sessUser = session.getAttribute(SessionConstants.USER);
            if (sessUser != null && sessUser instanceof User) {
                user = (User) sessUser;
                LOG.info(user.toString());
            } else {
                LOG.error("There's an active session (" + session.getId() + "), but no valid User object in it");
            }
        } else {
            LOG.info("No active session, returning null");
        }

        LOG.log(Logger.Action.RETURN, "logged in user");
        return user;
    }

    protected Integer getUriPathVariableAsInteger(HttpServletRequest request, String varName) {
        LOG.log(Logger.Action.BEGIN, "request", "varName");
        Integer retval = null;
        try {
            Object varVal = request.getAttribute(varName);
            if (varVal != null && varVal instanceof String) {
                retval = Integer.parseInt((String) varVal);
            }
        } catch (NumberFormatException e) {
            // silently ignore
        } finally {
            LOG.log(Logger.Action.RETURN, "requested variable");
        }
        return retval;
    }

    /**
     * Removes any active session for the current user.
     * 
     * @param request The current non-null HttpServletRequest associated with the user's request
     */
    protected void removeUserSession(HttpServletRequest request) {
        LOG.log(Logger.Action.BEGIN, "request");
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        LOG.log(Logger.Action.RETURN);
    }

    /**
     * Handles Retrieve by id REST requests.
     * 
     * @param id The id of the object to retrieve
     * @return The object being requested
     * @throws ServletException
     * @throws IOException
     */
    protected T processGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    /**
     * Handles Retrieve All REST requests, returning the appropriate objects given the context
     * 
     * @param request The current non-null HttpServletRequest associated with the user's request
     * @param response
     * @return The objects being requested
     * @throws ServletException
     * @throws IOException
     */
    protected Collection<T> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return null;
    }

    /**
     * Handles the Create REST requests.
     * @param request The current non-null HttpServletRequest associated with the user's request
     * @param response
     * @param postObject
     * @return The object that was Created
     * @throws ServletException
     * @throws IOException
     */
    protected T processPost(HttpServletRequest request, HttpServletResponse response, T postObject) throws ServletException, IOException {
        return null;
    }

    /**
     * Handles the Update REST requests.
     * @param request The current non-null HttpServletRequest associated with the user's request
     * @param response
     * @param putObject
     * @return The object that was Updated
     * @throws ServletException
     * @throws IOException
     */
    protected T processPut(HttpServletRequest request, HttpServletResponse response, T putObject) throws ServletException, IOException {
        return null;
    }

    /**
     * Handles the Delete REST requests.
     * 
     * @param request The current non-null HttpServletRequest associated with the user's request
     * @param response
     * @param deleteObject
     * @throws ServletException
     * @throws IOException
     */
    protected void processDelete(HttpServletRequest request, HttpServletResponse response, T deleteObject) throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Logger.Action.BEGIN, "request", "response");
        LOG.info("Path: " + request.getServletPath());

        HttpSession session = request.getSession(false);

        String returnDesc = "";
        String returnJsonString = null;

        try {
            if (requireValidSession() && session == null) {
                returnDesc = writeNotAuthorizedErrorResponse(response);
            } else {
                if (request.getAttribute(RestFilter.IS_COLLECTION) != null) {
                    Collection<T> objectToReturn = processGetAll(request, response);
                    returnDesc = LOG.getLastReturnedValueNames();
                    returnJsonString = objectsToJson(objectToReturn);
                } else {
                    T objectToReturn = processGet(request, response);
                    returnDesc = LOG.getLastReturnedValueNames();
                    returnJsonString = objectToJson(objectToReturn);
                }
                writeJsonResponse(response, returnJsonString);
            }
        } catch (ServletException | IOException e) {
            returnDesc = writeServerErrorResponse(response, e);
            return;
        } finally {
            LOG.log(Logger.Action.RETURN, returnDesc);
            LOG.logSequenceCalls();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Logger.Action.BEGIN, "request", "response");
        LOG.info("Path: " + request.getServletPath());

        String returnDesc = "";

        try {
            if (requireValidSession() && request.getSession(false) == null) {
                returnDesc = writeNotAuthorizedErrorResponse(response);
            } else {
                try {
                    T inputObject = jsonToObject(readRequestAsString(request));
                    try {
                        T objectToReturn = processPost(request, response, inputObject);
                        returnDesc = LOG.getLastReturnedValueNames();
                        writeJsonResponse(response, objectToJson(objectToReturn));
                    } catch (Exception e) {
                        returnDesc = writeServerErrorResponse(response, e);
                    }
                } catch (Exception e) {
                    returnDesc = writeClientErrorResponse(response, e);
                }
            }
        } finally {
            LOG.log(Logger.Action.RETURN, returnDesc);
            LOG.logSequenceCalls();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Logger.Action.BEGIN, "request", "response");
        LOG.info("Path: " + request.getServletPath());

        String returnDesc = "";

        try {
            if (requireValidSession() && request.getSession(false) == null) {
                returnDesc = writeNotAuthorizedErrorResponse(response);
            } else {
                try {
                    T inputObject = jsonToObject(readRequestAsString(request));
                    try {
                        T objectToReturn = processPut(request, response, inputObject);
                        returnDesc = LOG.getLastReturnedValueNames();
                        writeJsonResponse(response, objectToJson(objectToReturn));
                    } catch (Exception e) {
                        returnDesc = writeServerErrorResponse(response, e);
                    }
                } catch (Exception e) {
                    returnDesc = writeClientErrorResponse(response, e);
                }
            }
        } finally {
            LOG.log(Logger.Action.RETURN, returnDesc);
            LOG.logSequenceCalls();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Logger.Action.BEGIN, "request", "response");
        LOG.info("Path: " + request.getServletPath());

        String returnDesc = "";

        try {
            if (requireValidSession() && request.getSession(false) == null) {
                returnDesc = writeNotAuthorizedErrorResponse(response);
            } else {
                try {
                    T inputObject = jsonToObject(readRequestAsString(request));
                    try {
                        processDelete(request, response, inputObject);
                        returnDesc = LOG.getLastReturnedValueNames();
                    } catch (Exception e) {
                        returnDesc = writeServerErrorResponse(response, e);
                    }
                } catch (Exception e) {
                    returnDesc = writeClientErrorResponse(response, e);
                }
            }
        } finally {
            LOG.log(Logger.Action.RETURN, returnDesc);
            LOG.logSequenceCalls();
        }
    }

    /**
     * Sets the response status code to 204 - Success but no content
     * 
     * @param response The current non-null HttpServletResponse associated with the user's request
     */
    protected void writeDeleteSuccessfulResponse(HttpServletResponse response) {
        LOG.log(Logger.Action.BEGIN, "response");
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        LOG.log(Logger.Action.RETURN, "204 delete successful");
    }

    private String writeClientErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        LOG.log(Logger.Action.BEGIN, "response");
        LOG.error(e);

        PrintWriter out;
        try {
            out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print("{ \"errorMessage\": \"Sorry, but there was something wrong with your request :(\"}");
            out.flush();
        } finally {
            LOG.log(Logger.Action.RETURN, "400 bad request");
        }
        return "400 bad request";
    }

    private String writeServerErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        LOG.log(Logger.Action.BEGIN, "response");
        LOG.error(e);

        PrintWriter out;
        try {
            out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print("{ \"errorMessage\": \"Sorry, but someting broke.  Please try again later :(\"}");
            out.flush();
        } finally {
            LOG.log(Logger.Action.RETURN, "500 internal error");
        }
        return "500 internal error";
    }

    private String writeNotAuthorizedErrorResponse(HttpServletResponse response) throws IOException {
        LOG.log(Logger.Action.BEGIN, "response");
        PrintWriter out;
        try {
            out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print("{ \"errorMessage\": \"I'm sorry Dave, but I cannot allow you to do that :(\"}");
            out.flush();
        } finally {
            LOG.log(Logger.Action.RETURN, "401 unauthorized");
        }
        return "401 unauthorized";
    }

    private void writeJsonResponse(HttpServletResponse response, String returnJsonString) throws IOException {
        LOG.log(Logger.Action.BEGIN, "response", "returnJsonString");
        try {
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(returnJsonString);
            out.flush();
        } finally {
            LOG.log(Logger.Action.RETURN);
        }
    }

    private String readRequestAsString(HttpServletRequest request) throws IOException {
        LOG.log(Logger.Action.BEGIN, "request");
        try {
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            LOG.info("INPUT: " + buffer.toString());
            return buffer.toString();
        } finally {
            LOG.log(Logger.Action.RETURN, "payload text");
        }
    }

    private T jsonToObject(String json) throws IOException {
        LOG.log(Logger.Action.BEGIN, "JSON");
        LOG.info("json: " + json);

        T retval;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectReader objectReader = objectMapper.readerFor(type);
            retval = objectReader.readValue(json);
        } finally {
            LOG.log(Logger.Action.RETURN, "java objects");
        }
        return retval;
    }

    private String objectToJson(T objectToConvert) throws JsonProcessingException {
        LOG.log(Logger.Action.BEGIN, "objects to convert");

        String returnJsonString;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            returnJsonString = objectMapper.writeValueAsString(objectToConvert);
            LOG.info("returnJsonString: " + returnJsonString);
        } finally {
            LOG.log(Logger.Action.RETURN, "JSON");
        }
        return returnJsonString;
    }

    private String objectsToJson(Collection<T> objectsToConvert) throws JsonProcessingException {
        LOG.log(Logger.Action.BEGIN, "objects to convert");

        String returnJsonString;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            returnJsonString = objectMapper.writeValueAsString(objectsToConvert);
            LOG.info("returnJsonString: " + returnJsonString);
        } finally {
            LOG.log(Logger.Action.RETURN, "JSON");
        }
        return returnJsonString;

    }
}
