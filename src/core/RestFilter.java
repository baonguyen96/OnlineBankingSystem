package core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebFilter("/RestFilter")
public class RestFilter implements Filter {
    private static final Logger LOG = new Logger(RestFilter.class);

    public static final String IS_COLLECTION = "RestFilter.IS_COLLECTION";

    static Map<String, String> regExToPath = null;
    static Map<String, Map<Integer, String>> regExVars = null;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String contextPath = httpRequest.getServletContext().getContextPath();
        String uri = httpRequest.getRequestURI();
        String urlPattern = uri.substring(contextPath.length());

        System.out.println("RestFilter: contextPath = " + contextPath + "; uri = " + uri + "; urlPattern = " + urlPattern);

        initializeIfNeeded(request);

        for (String regex : regExToPath.keySet()) {
            if (urlPattern.matches(regex)) {
                String path = regExToPath.get(regex);
                System.out.println("urlPattern matches for path: " + path);
                Map<Integer, String> vars = regExVars.get(regex);

                String[] parts = urlPattern.split("/");
                for (Integer varIndex : vars.keySet()) {
                    if (varIndex < parts.length) {
                        String varName = vars.get(varIndex);
                        String varValue = parts[varIndex];
                        System.out.println("\tAdding Attribute: " + varName + "=" + varValue);
                        request.setAttribute(varName, varValue);
                    } else {
                        request.setAttribute(IS_COLLECTION, true);
                    }
                }
                RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
                requestDispatcher.forward(request, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    synchronized void initializeIfNeeded(ServletRequest request) {
        if (regExToPath == null || regExVars == null) {
            regExToPath = new LinkedHashMap<>();
            regExVars = new LinkedHashMap<>();
            scanServletsForAnnotations(request.getServletContext().getServletRegistrations());
        }
    }

    void scanServletsForAnnotations(Map<String, ? extends ServletRegistration> servletRegistrations) {
        System.out.println("Begin Scanning Registered Servlets for Annotations:");
        for (ServletRegistration servletRegistration : servletRegistrations.values()) {
            System.out.println("\tFound Servlet: " + servletRegistration.getClassName());
            try {
                Class<?> clazz = Class.forName(servletRegistration.getClassName());
                Annotation annotation = clazz.getAnnotation(WebServlet.class);
                if (annotation != null) {
                    WebServlet servlet = (WebServlet) annotation;
                    System.out.println("\t\tServlet has WebServlet annotation, checking URL patterns for URI path variables");

                    for (String urlPattern : servlet.value()) {
                        scanAndRegisterUriPathVariables(urlPattern);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Scanning Servlets for Annotations Complete");
    }

    void scanAndRegisterUriPathVariables(String urlPattern) {
        System.out.println("\t\t\tChecking URL Pattern: " + urlPattern);
        if (urlPattern.matches("/api/.*/\\{.+\\}.*")) { // has at least 1 variable
            System.out.println("\t\t\t\tURL Pattern appears to have path variables");

            Map<Integer, String> vars = new LinkedHashMap<>();

            String[] parts = urlPattern.split("/");

            String regex = "";
            int i = 0;
            for (String part : parts) {
                if (part.matches("\\{.+\\}")) {
                    String varName = part.substring(part.indexOf("{") + 1, part.lastIndexOf("}"));
                    vars.put(i, varName);
                    if (i < parts.length - 1) regex += "\\/(\\{" + varName + "\\}|-{0,1}\\d+)";
                    else regex += "(/\\{" + varName + "\\}|/-{0,1}\\d[^\\/?]+){0,1}";
                } else {
                    if (!part.isEmpty()) regex += "\\/" + part;
                }
                i++;
            }
            regex += "(\\?.*){0,1}";

            System.out.println("\t\t\t\t\tConstructed RegEx = " + regex + " to match URIs with differernt variables");
            System.out.println("\t\t\t\t\tRegEx Matches URL Pattern: " + urlPattern.matches(regex));
            System.out.println("\t\t\t\t\tThe following variables were found:" + vars.values());

            if (!regExToPath.containsKey(regex)) {
                regExToPath.put(regex, urlPattern);
                regExVars.put(regex, vars);
                System.out.println("\t\t\t\t\tRegEx and Variables were registered for URL Pattern");
            } else {
                System.err.println("\t\t\t\t\tRegEx already registered for " + regExToPath.get(regex));
            }
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }
}
