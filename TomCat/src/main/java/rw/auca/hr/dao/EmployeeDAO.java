package rw.auca.hr.dao;

import rw.auca.hr.model.Employee;
import rw.auca.hr.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeDAO {
    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());
    
    private static final String SELECT_ALL_EMPLOYEES = 
            "SELECT emp_id, name, gender, start_date, position_name, qualification FROM employees";
    
    private static final String SELECT_EMPLOYEE_BY_ID = 
            "SELECT emp_id, name, gender, start_date, position_name, qualification FROM employees WHERE emp_id = ?";
    
    private static final String INSERT_EMPLOYEE = 
            "INSERT INTO employees (name, gender, start_date, position_name, qualification) VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_EMPLOYEE = 
            "UPDATE employees SET name = ?, gender = ?, start_date = ?, position_name = ?, qualification = ? WHERE emp_id = ?";
    
    private static final String DELETE_EMPLOYEE = 
            "DELETE FROM employees WHERE emp_id = ?";
    
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_EMPLOYEES);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Employee employee = mapResultSetToEmployee(rs);
                employees.add(employee);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving employees", e);
        }
        
        return employees;
    }
    
    public Employee getEmployeeById(int empId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_EMPLOYEE_BY_ID)) {
            
            pstmt.setInt(1, empId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving employee with ID " + empId, e);
        }
        
        return null;
    }
    
    public boolean addEmployee(Employee employee) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_EMPLOYEE)) {
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getGender());
            pstmt.setDate(3, new java.sql.Date(employee.getStartDate().getTime()));
            pstmt.setString(4, employee.getPositionName());
            pstmt.setString(5, employee.getQualification());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding employee", e);
            return false;
        }
    }
    
    public boolean updateEmployee(Employee employee) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_EMPLOYEE)) {
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getGender());
            pstmt.setDate(3, new java.sql.Date(employee.getStartDate().getTime()));
            pstmt.setString(4, employee.getPositionName());
            pstmt.setString(5, employee.getQualification());
            pstmt.setInt(6, employee.getEmpId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating employee", e);
            return false;
        }
    }
    
    public boolean deleteEmployee(int empId) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_EMPLOYEE)) {
            
            pstmt.setInt(1, empId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting employee", e);
            return false;
        }
    }
    
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmpId(rs.getInt("emp_id"));
        employee.setName(rs.getString("name"));
        employee.setGender(rs.getString("gender"));
        employee.setStartDate(rs.getDate("start_date"));
        employee.setPositionName(rs.getString("position_name"));
        employee.setQualification(rs.getString("qualification"));
        return employee;
    }
}
