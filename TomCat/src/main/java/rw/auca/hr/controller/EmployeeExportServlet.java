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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/employee/export")
public class EmployeeExportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EmployeeExportServlet.class.getName());
    
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response headers for CSV download
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=employees.csv");
        
        try (PrintWriter writer = response.getWriter()) {
            // Write CSV header
            writer.println("ID,Name,Position,Start Date,Gender,Qualification");
            
            // Get all employees
            List<Employee> employees = employeeDAO.getAllEmployees();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            
            // Write employee data
            for (Employee employee : employees) {
                StringBuilder line = new StringBuilder();
                line.append(employee.getEmpId()).append(",");
                line.append(escapeCSV(employee.getName())).append(",");
                line.append(escapeCSV(employee.getPositionName())).append(",");
                line.append(employee.getStartDate() != null ? 
                        dateFormat.format(employee.getStartDate()) : "N/A").append(",");
                line.append(escapeCSV(employee.getGender())).append(",");
                line.append(escapeCSV(employee.getQualification() != null ? 
                        employee.getQualification() : "N/A"));
                
                writer.println(line.toString());
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating CSV", e);
            response.setContentType("text/plain");
            response.getWriter().write("Error generating CSV: " + e.getMessage());
        }
    }
    
    /**
     * Escapes special characters in CSV fields
     */
    private String escapeCSV(String field) {
        if (field == null) {
            return "";
        }
        
        // If field contains commas, quotes, or newlines, wrap in quotes and escape quotes
        if (field.contains("\"") || field.contains(",") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
