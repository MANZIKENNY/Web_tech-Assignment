package rw.auca.hr.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import rw.auca.hr.dao.EmployeeDAO;
import rw.auca.hr.model.Employee;
import rw.auca.hr.util.SessionManager;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get user information from session
        String username = SessionManager.getUsername(request);
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) request.getSession().getAttribute("roles");
        String roleDisplay = (roles != null && !roles.isEmpty()) ? roles.get(0) : "User";
        
        // Get employees from database
        List<Employee> employees = employeeDAO.getAllEmployees();
        
        // Set attributes for JSP
        request.setAttribute("username", username);
        request.setAttribute("roleDisplay", roleDisplay);
        request.setAttribute("employees", employees);
        
        // Forward to the no-JSTL version until you add the JSTL dependencies
        request.getRequestDispatcher("/pages/Dashboard.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle logout action
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            SessionManager.logout(request);
            response.sendRedirect(request.getContextPath() + "/auth");
        }
    }
}
