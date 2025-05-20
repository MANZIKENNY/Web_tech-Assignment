package rw.auca.hr.dao;

import rw.auca.hr.model.User;
import rw.auca.hr.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    
    public User findByUsername(String username) {
        String sql = "SELECT u.id, u.username, u.password, u.profile_picture, r.id as role_id, r.name as role_name " +
                     "FROM users u " +
                     "JOIN user_roles ur ON u.id = ur.user_id " +
                     "JOIN roles r ON ur.role_id = r.id " +
                     "WHERE u.username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                User user = null;
                ArrayList<String> roles = new ArrayList<>();
                
                while (rs.next()) {
                    if (user == null) {
                        user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(rs.getString("password"));
                        // Use setProfilePicture consistently
                        user.setProfilePicture(rs.getString("profile_picture"));
                    }
                    roles.add(rs.getString("role_name"));
                }
                
                if (user != null) {
                    user.setRoles(roles);
                }
                
                return user;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username: " + username, e);
            return null;
        }
    }
    
    public boolean updateProfilePicture(int userId, String fileName) {
        String sql = "UPDATE users SET profile_picture = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fileName);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating profile picture for user ID: " + userId, e);
            return false;
        }
    }
}
