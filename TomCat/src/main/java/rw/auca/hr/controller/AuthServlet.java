package rw.auca.hr.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rw.auca.hr.util.DatabaseUtil;
import rw.auca.hr.util.SessionManager;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect if already authenticated
        if (SessionManager.isAuthenticated(request)) {
            if (SessionManager.hasRole(request, "ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/admin");  // Changed to /admin
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
            return;
        }
        
        request.getRequestDispatcher("/pages/Authentication.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        switch (action) {
            case "login":
                handleLogin(request, response);
                break;
            case "register":
                handleRegister(request, response);
                break;
            case "logout":
                handleLogout(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/auth");
                break;
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, hashPassword(password));
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("id");
                        List<String> roles = getUserRoles(conn, userId);
                        
                        // Create authenticated session
                        SessionManager.createAuthenticatedSession(request, userId, username, roles);
                        
                        // Redirect based on role
                        if (roles.contains("ADMIN")) {
                            response.sendRedirect(request.getContextPath() + "/admin");  // Changed to /admin
                        } else {
                            response.sendRedirect(request.getContextPath() + "/dashboard");
                        }
                        return;
                    }
                }
            }
            
            request.setAttribute("loginError", "Invalid username or password");
            request.getRequestDispatcher("/pages/Authentication.jsp").forward(request, response);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Login error", e);
            request.setAttribute("loginError", "System error occurred");
            request.getRequestDispatcher("/pages/Authentication.jsp").forward(request, response);
        }
    }
    
    private List<String> getUserRoles(Connection conn, int userId) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT r.name FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("name"));
                }
            }
        }
        return roles;
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            if (!isUsernameAvailable(conn, username)) {
                request.setAttribute("registerError", "Username already exists");
                request.getRequestDispatcher("/pages/Authentication.jsp").forward(request, response);
                return;
            }
            
            conn.setAutoCommit(false);
            try {
                int userId = createUser(conn, username, password);
                assignDefaultRole(conn, userId);
                conn.commit();
                
                request.setAttribute("registerSuccess", "Registration successful! Please log in.");
                request.getRequestDispatcher("/pages/Authentication.jsp").forward(request, response);
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Registration error", e);
            request.setAttribute("registerError", "System error occurred");
            request.getRequestDispatcher("/pages/Authentication.jsp").forward(request, response);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionManager.logout(request);
        response.sendRedirect(request.getContextPath() + "/auth");
    }
    
    private boolean isUsernameAvailable(Connection conn, String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        }
    }
    
    private int createUser(Connection conn, String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to get generated user ID");
            }
        }
    }
    
    private void assignDefaultRole(Connection conn, int userId) throws SQLException {
        // Changed role_id from 2 (HR) to 3 (EMPLOYEE)
        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, 3)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            
            return hashText;
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
