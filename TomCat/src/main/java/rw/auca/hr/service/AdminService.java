package rw.auca.hr.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import rw.auca.hr.util.DatabaseUtil;

public class AdminService {
    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());
    
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Modified query to match your actual database schema
            String sql = "SELECT u.id, u.username, u.profile_picture FROM users u ORDER BY u.username";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    int userId = rs.getInt("id");
                    
                    user.put("id", userId);
                    user.put("username", rs.getString("username"));
                    user.put("profilePicture", rs.getString("profile_picture"));
                    
                    // Default values for missing columns
                    user.put("lastLogin", "Not tracked");
                    user.put("active", true);
                    
                    // Get user roles
                    List<String> roles = getUserRoles(conn, userId);
                    user.put("roles", roles);
                    
                    // Mark if this user is likely the current admin (ID 1)
                    user.put("isCurrentUser", userId == 1);
                    
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all users", e);
        }
        
        return users;
    }
    
    private List<String> getUserRoles(Connection conn, int userId) throws SQLException {
        List<String> roles = new ArrayList<>();
        
        // Check if user_roles and roles tables exist
        try {
            String sql = "SELECT r.name FROM roles r " +
                         "JOIN user_roles ur ON r.id = ur.role_id " +
                         "WHERE ur.user_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        roles.add(rs.getString("name"));
                    }
                }
            }
        } catch (SQLException e) {
            // If tables don't exist or error occurs, assign default role
            if (userId == 1) {
                roles.add("ADMIN");
            } else {
                roles.add("EMPLOYEE");
            }
        }
        
        // If no roles were found, assign a default role
        if (roles.isEmpty()) {
            if (userId == 1) {
                roles.add("ADMIN");
            } else {
                roles.add("EMPLOYEE");
            }
        }
        
        return roles;
    }
    
    public Map<String, Object> getUserById(int userId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Modified query to match your actual schema
            String sql = "SELECT u.id, u.username, u.profile_picture FROM users u WHERE u.id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        
                        user.put("id", rs.getInt("id"));
                        user.put("username", rs.getString("username"));
                        user.put("profilePicture", rs.getString("profile_picture"));
                        
                        // Default values for missing columns
                        user.put("lastLogin", "Not tracked");
                        user.put("active", true);
                        
                        List<String> roles = getUserRoles(conn, userId);
                        user.put("roles", roles);
                        
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by ID: " + userId, e);
        }
        
        return null;
    }
    
    public Map<Integer, String> getAllRoles() {
        Map<Integer, String> roles = new LinkedHashMap<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            try {
                String sql = "SELECT id, name, description FROM roles ORDER BY id";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    
                    while (rs.next()) {
                        int roleId = rs.getInt("id");
                        String roleName = rs.getString("name");
                        String roleDesc = rs.getString("description");
                        
                        roles.put(roleId, roleName + (roleDesc != null ? " - " + roleDesc : ""));
                    }
                }
            } catch (SQLException e) {
                // If roles table doesn't exist, provide default roles
                roles.put(1, "ADMIN - Administrator");
                roles.put(2, "HR - Human Resources");
                roles.put(3, "EMPLOYEE - Employee");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all roles", e);
            
            // Provide default roles if connection failed
            roles.put(1, "ADMIN - Administrator");
            roles.put(2, "HR - Human Resources");
            roles.put(3, "EMPLOYEE - Employee");
        }
        
        return roles;
    }
    
    public boolean addUser(String username, String password, String[] roles, boolean active) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Check if username already exists
                if (!isUsernameAvailable(conn, username)) {
                    return false;
                }
                
                // Insert user - modified for actual schema
                String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                
                int userId;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, username);
                    ps.setString(2, hashPassword(password));
                    
                    ps.executeUpdate();
                    
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            userId = rs.getInt(1);
                        } else {
                            throw new SQLException("Failed to get generated user ID");
                        }
                    }
                }
                
                // Try to assign roles if the tables exist
                try {
                    if (roles != null && roles.length > 0) {
                        assignRoles(conn, userId, roles);
                    } else {
                        // Default to EMPLOYEE role if none specified
                        assignRoles(conn, userId, new String[]{"EMPLOYEE"});
                    }
                } catch (SQLException e) {
                    // If role tables don't exist, just continue
                    LOGGER.log(Level.INFO, "Could not assign roles, role tables may not exist", e);
                }
                
                conn.commit();
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user: " + username, e);
            return false;
        }
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
    
    public boolean updateUser(int userId, String[] roles, boolean active, boolean resetPassword, String newPassword) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Reset password if requested
                if (resetPassword && newPassword != null && !newPassword.isEmpty()) {
                    String updatePasswordSql = "UPDATE users SET password = ? WHERE id = ?";
                    
                    try (PreparedStatement passwordStmt = conn.prepareStatement(updatePasswordSql)) {
                        passwordStmt.setString(1, hashPassword(newPassword));
                        passwordStmt.setInt(2, userId);
                        
                        passwordStmt.executeUpdate();
                    }
                }
                
                // Try to update roles if the tables exist
                try {
                    if (roles != null && roles.length > 0) {
                        // Remove existing roles
                        String deleteRolesSql = "DELETE FROM user_roles WHERE user_id = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRolesSql)) {
                            deleteStmt.setInt(1, userId);
                            deleteStmt.executeUpdate();
                        }
                        
                        // Assign new roles
                        assignRoles(conn, userId, roles);
                    }
                } catch (SQLException e) {
                    // If role tables don't exist, just continue
                    LOGGER.log(Level.INFO, "Could not update roles, role tables may not exist", e);
                }
                
                conn.commit();
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user ID: " + userId, e);
            return false;
        }
    }
    
    public boolean deleteUser(int userId) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Try to delete user roles first if table exists
                try {
                    String deleteRolesSql = "DELETE FROM user_roles WHERE user_id = ?";
                    try (PreparedStatement deleteRolesStmt = conn.prepareStatement(deleteRolesSql)) {
                        deleteRolesStmt.setInt(1, userId);
                        deleteRolesStmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    // If roles table doesn't exist, just continue
                }
                
                // Delete user
                String deleteUserSql = "DELETE FROM users WHERE id = ?";
                
                try (PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql)) {
                    deleteUserStmt.setInt(1, userId);
                    int rowsAffected = deleteUserStmt.executeUpdate();
                    
                    conn.commit();
                    return rowsAffected > 0;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user ID: " + userId, e);
            return false;
        }
    }
    
    private void assignRoles(Connection conn, int userId, String[] roleNames) throws SQLException {
        Map<String, Integer> roleMap = getRoleMap(conn);
        
        String insertRoleSql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        
        try (PreparedStatement insertRoleStmt = conn.prepareStatement(insertRoleSql)) {
            for (String roleName : roleNames) {
                Integer roleId = roleMap.get(roleName);
                
                if (roleId != null) {
                    insertRoleStmt.setInt(1, userId);
                    insertRoleStmt.setInt(2, roleId);
                    insertRoleStmt.addBatch();
                }
            }
            
            insertRoleStmt.executeBatch();
        }
    }
    
    private Map<String, Integer> getRoleMap(Connection conn) throws SQLException {
        Map<String, Integer> roleMap = new HashMap<>();
        
        String sql = "SELECT id, name FROM roles";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                roleMap.put(rs.getString("name"), rs.getInt("id"));
            }
        }
        
        return roleMap;
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
