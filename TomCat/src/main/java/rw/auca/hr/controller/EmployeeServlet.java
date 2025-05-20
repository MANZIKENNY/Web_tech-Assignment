package rw.auca.hr.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import rw.auca.hr.dao.EmployeeDAO;
import rw.auca.hr.model.Employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/employee/get", "/employee/add", "/employee/update", "/employee/delete"})
public class EmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EmployeeServlet.class.getName());
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        if ("/employee/get".equals(servletPath)) {
            // Handle GET request for fetching employee data
            try {
                int empId = Integer.parseInt(request.getParameter("id"));
                Employee employee = employeeDAO.getEmployeeById(empId);
                
                if (employee != null) {
                    // Create a JSON response manually instead of using JSONObject
                    String jsonResponse = String.format(
                        "{\"empId\":%d,\"name\":\"%s\",\"gender\":\"%s\",\"startDate\":%d,\"positionName\":\"%s\",\"qualification\":\"%s\"}",
                        employee.getEmpId(),
                        escapeJson(employee.getName()),
                        escapeJson(employee.getGender()),
                        employee.getStartDate().getTime(),
                        escapeJson(employee.getPositionName()),
                        escapeJson(employee.getQualification() != null ? employee.getQualification() : "")
                    );
                    
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    
                    PrintWriter out = response.getWriter();
                    out.print(jsonResponse);
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                LOGGER.log(Level.WARNING, "Invalid employee ID format", e);
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        
        switch (servletPath) {
            case "/employee/add":
                handleAddEmployee(request, response);
                break;
            case "/employee/update":
                handleUpdateEmployee(request, response);
                break;
            case "/employee/delete":
                handleDeleteEmployee(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    private void handleAddEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String name = request.getParameter("emp-name");
            String gender = request.getParameter("emp-gender");
            String dateString = request.getParameter("emp-date");
            String position = request.getParameter("emp-position");
            String qualification = request.getParameter("emp-qualification");
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(dateString);
            
            Employee employee = new Employee();
            employee.setName(name);
            employee.setGender(gender);
            employee.setStartDate(startDate);
            employee.setPositionName(position);
            employee.setQualification(qualification);
            
            boolean success = employeeDAO.addEmployee(employee);
            
            if (success) {
                // Set success message
                request.getSession().setAttribute("message", "Employee added successfully");
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                // Set error message
                request.getSession().setAttribute("error", "Failed to add employee");
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
            
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error parsing date", e);
            request.getSession().setAttribute("error", "Invalid date format");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
    
    private void handleUpdateEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int empId = Integer.parseInt(request.getParameter("emp-id"));
            String name = request.getParameter("emp-name");
            String gender = request.getParameter("emp-gender");
            String dateString = request.getParameter("emp-date");
            String position = request.getParameter("emp-position");
            String qualification = request.getParameter("emp-qualification");
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(dateString);
            
            Employee employee = new Employee();
            employee.setEmpId(empId);
            employee.setName(name);
            employee.setGender(gender);
            employee.setStartDate(startDate);
            employee.setPositionName(position);
            employee.setQualification(qualification);
            
            boolean success = employeeDAO.updateEmployee(employee);
            
            if (success) {
                request.getSession().setAttribute("message", "Employee updated successfully");
            } else {
                request.getSession().setAttribute("error", "Failed to update employee");
            }
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
            
        } catch (ParseException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error updating employee", e);
            request.getSession().setAttribute("error", "Invalid input data");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
    
    private void handleDeleteEmployee(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int empId = Integer.parseInt(request.getParameter("emp-id"));
            
            boolean success = employeeDAO.deleteEmployee(empId);
            
            if (success) {
                request.getSession().setAttribute("message", "Employee deleted successfully");
            } else {
                request.getSession().setAttribute("error", "Failed to delete employee");
            }
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error deleting employee", e);
            request.getSession().setAttribute("error", "Invalid employee ID");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
    
    /**
     * Simple JSON string escaper
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
