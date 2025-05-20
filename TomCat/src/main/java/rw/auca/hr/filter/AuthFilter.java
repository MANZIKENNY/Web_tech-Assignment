package rw.auca.hr.filter;

import java.io.IOException;
import java.util.Enumeration;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String loginURI = httpRequest.getContextPath() + "/auth";
        String requestURI = httpRequest.getRequestURI();

        boolean loggedIn = (session != null && session.getAttribute("authenticated") != null && (boolean) session.getAttribute("authenticated"));
        boolean loginRequest = requestURI.equals(loginURI) || requestURI.equals(loginURI + "/");
        boolean staticResource = requestURI.startsWith(httpRequest.getContextPath() + "/static/");

        // Debugging: Print session details
        System.out.println("Request URI: " + requestURI);
        if (session != null) {
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();
            }
        }

        if (loggedIn || loginRequest || staticResource) {
            chain.doFilter(request, response); // User is logged in, requesting login page, or requesting static resources, so continue.
        } else {
            httpResponse.sendRedirect(loginURI); // User is not logged in, redirect to login page.
        }
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
