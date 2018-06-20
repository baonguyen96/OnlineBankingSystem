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

public abstract class JsonServletBase<T extends Object> extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(JsonServletBase.class.getName());
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public JsonServletBase() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected boolean requireValidSession() {
        return true;
    }

    protected abstract T processGet(HttpServletRequest request, HttpServletResponse response, Long id) throws ServletException, IOException;

    protected abstract Collection<T> processGetAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected abstract T processPost(HttpServletRequest request, HttpServletResponse response, T postObject) throws ServletException, IOException;

    protected abstract T processPut(HttpServletRequest request, HttpServletResponse response, T putObject) throws ServletException, IOException;

    protected abstract void processDelete(HttpServletRequest request, HttpServletResponse response, T deleteObject) throws ServletException, IOException;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.log(Level.INFO, "doGet(): BEGIN");

        HttpSession session = request.getSession(false);
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
