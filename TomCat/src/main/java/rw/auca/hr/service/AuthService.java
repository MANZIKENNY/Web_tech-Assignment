package rw.auca.hr.service;

import rw.auca.hr.model.User;
import rw.auca.hr.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class AuthService {

    public User authenticate(String username, String password) {
        String hashedPassword = hashPassword(password);
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, username FROM users WHERE username = ? AND password = ?")) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Use no-arg constructor and setter methods
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    // Don't set password for security
                    
                    ArrayList<String> roles = new ArrayList<>(getUserRoles(user.getId(), conn));
                    user.setRoles(roles);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean isUsernameAvailable(String username) {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM users WHERE username = ?")) {
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean registerUser(String username, String password, String email) {
        String hashedPassword = hashPassword(password);
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert user
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, password) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, username);
                    ps.setString(2, hashedPassword);
                    
                    int affectedRows = ps.executeUpdate();
                    if (affectedRows == 0) {
                        return false;
                    }
                    
                    int userId;
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            userId = rs.getInt(1);
                        } else {
                            return false;
                        }
                    }
                    
                    // Assign default role
                    try (PreparedStatement rolePs = conn.prepareStatement(
                            "INSERT INTO user_roles (user_id, role_id) VALUES (?, 2)")) { 
                        // Assuming role_id 2 is for MANAGER/HR role
                        rolePs.setInt(1, userId);
                        rolePs.executeUpdate();
                    }
                    
                    conn.commit();
                    return true;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private List<String> getUserRoles(int userId, Connection conn) throws SQLException {
        List<String> roles = new ArrayList<>();
        
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT r.name FROM roles r " +
                "JOIN user_roles ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?")) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    roles.add(rs.getString("name"));
                }
            }
        }
        
        return roles;
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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
