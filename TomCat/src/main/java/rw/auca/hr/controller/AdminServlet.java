package rw.auca.hr.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import rw.auca.hr.model.User;
import rw.auca.hr.service.AdminService;
import rw.auca.hr.util.ServerStats;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@WebServlet({"/admin", "/admin/*"})
public class AdminServlet extends HttpServlet {
    private AdminService adminService = new AdminService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // For the root /admin URL, pathInfo will be null
        if (pathInfo == null || pathInfo.equals("/")) {
            // Set current date/time in UTC format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            req.setAttribute("currentDateTime", dateFormat.format(new Date()));
            
            // Set the currently logged in admin's info
            HttpSession session = req.getSession(false);
            if (session != null) {
                // Check if we have a full User object in the session
                Object userObj = session.getAttribute("user");
                if (userObj instanceof User) {
                    User user = (User) userObj;
                    req.setAttribute("username", user.getUsername());
                } else {
                    // Fall back to session attributes if no User object
                    String username = (String) session.getAttribute("username");
                    if (username != null) {
                        req.setAttribute("username", username);
                    } else {
                        req.setAttribute("username", "Administrator");
                    }
                }
            } else {
                req.setAttribute("username", "Administrator");
            }
            req.setAttribute("roleDisplay", "Administrator");
            
            // Get all users for the admin panel with their roles
            List<Map<String, Object>> users = adminService.getAllUsers();
            
            // Mark current user (get from session if possible)
            Integer currentUserId = session != null ? (Integer) session.getAttribute("userId") : 1;
            for (Map<String, Object> userMap : users) {
                userMap.put("isCurrentUser", userMap.get("id").equals(currentUserId));
            }
            
            req.setAttribute("users", users);
            
            // Get available roles for assignment
            Map<Integer, String> availableRoles = adminService.getAllRoles();
            req.setAttribute("availableRoles", availableRoles);
            
            req.getRequestDispatcher("/pages/Admin.jsp").forward(req, resp);
            return;
        }
        
        // Handle specific admin endpoints
        switch (pathInfo) {
            case "/get-user":
                handleGetUser(req, resp);
                break;
            case "/export-users":
                handleExportUsers(req, resp);
                break;
            case "/stats":
                handleStats(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/admin");
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // If no specific action is provided
        if (pathInfo == null) {
            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }
        
        switch (pathInfo) {
            case "/add-user":
                handleAddUser(req, resp);
                break;
            case "/update-user":
                handleUpdateUser(req, resp);
                break;
            case "/delete-user":
                handleDeleteUser(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/admin");
                break;
        }
    }
    
    private void handleGetUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userId = req.getParameter("id");
        if (userId != null) {
            Map<String, Object> user = adminService.getUserById(Integer.parseInt(userId));
            if (user != null) {
                resp.setContentType("application/json");
                resp.getWriter().write(convertToJson(user));
                return;
            }
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    
    private void handleAddUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String[] roles = req.getParameterValues("roles");
        String status = req.getParameter("status");
        
        boolean success = adminService.addUser(username, password, roles, "active".equals(status));
        
        if (success) {
            req.getSession().setAttribute("message", "User successfully created");
        } else {
            req.getSession().setAttribute("error", "Failed to create user");
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
    
    private void handleUpdateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = Integer.parseInt(req.getParameter("user-id"));
        String[] roles = req.getParameterValues("roles");
        boolean active = "active".equals(req.getParameter("status"));
        
        boolean resetPassword = req.getParameter("reset-password") != null;
        String newPassword = resetPassword ? req.getParameter("new-password") : null;
        
        boolean success = adminService.updateUser(userId, roles, active, resetPassword, newPassword);
        
        if (success) {
            req.getSession().setAttribute("message", "User successfully updated");
        } else {
            req.getSession().setAttribute("error", "Failed to update user");
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
    
    private void handleDeleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int userId = Integer.parseInt(req.getParameter("user-id"));
        
        // Get current user ID from session if possible
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId == userId) {
                req.getSession().setAttribute("error", "Cannot delete your own account");
                resp.sendRedirect(req.getContextPath() + "/admin");
                return;
            }
        }
        
        boolean success = adminService.deleteUser(userId);
        
        if (success) {
            req.getSession().setAttribute("message", "User successfully deleted");
        } else {
            req.getSession().setAttribute("error", "Failed to delete user");
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
    
    private void handleExportUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Map<String, Object>> users = adminService.getAllUsers();
        
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"users-" + 
            new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".csv\"");
        
        try (PrintWriter writer = resp.getWriter()) {
            // Write header
            writer.println("ID,Username,Roles,Last Login,Status");
            
            // Write data
            for (Map<String, Object> user : users) {
                StringBuilder line = new StringBuilder();
                line.append(user.get("id")).append(",");
                line.append(user.get("username")).append(",");
                
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) user.get("roles");
                line.append("\"").append(String.join("; ", roles)).append("\",");
                
                Object lastLogin = user.get("lastLogin");
                line.append(lastLogin != null ? lastLogin : "Never").append(",");
                
                line.append((Boolean) user.get("active") ? "Active" : "Inactive");
                
                writer.println(line);
            }
        }
    }
    
    private void handleStats(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeUsers", ServerStats.getActiveSessions());
        stats.put("totalRequests", ServerStats.getTotalRequests());
        
        // Current UTC time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        stats.put("currentDateTime", dateFormat.format(new Date()));
        
        resp.getWriter().write(convertToJson(stats));
    }
    
    private String convertToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            
            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) value;
                json.append("[");
                boolean firstItem = true;
                for (String item : list) {
                    if (!firstItem) {
                        json.append(",");
                    }
                    firstItem = false;
                    json.append("\"").append(escapeJson(item)).append("\"");
                }
                json.append("]");
            } else {
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        
        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\b': escaped.append("\\b"); break;
                case '\f': escaped.append("\\f"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default: escaped.append(c);
            }
        }
        return escaped.toString();
    }
}
