package rw.auca.hr.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import rw.auca.hr.dao.UserDAO;
import rw.auca.hr.model.User;
import rw.auca.hr.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/user/profile")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,  // 1 MB
    maxFileSize = 1024 * 1024 * 10,   // 10 MB
    maxRequestSize = 1024 * 1024 * 50  // 50 MB
)
public class UserProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserProfileServlet.class.getName());
    private static final String UPLOAD_DIRECTORY = "assets/profile";
    
    private final UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        
        // Create upload directory if it doesn't exist
        String applicationPath = request.getServletContext().getRealPath("");
        String uploadPath = applicationPath + UPLOAD_DIRECTORY;
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        try {
            // Process the uploaded file
            Part filePart = request.getPart("profile-picture");
            if (filePart == null) {
                request.getSession().setAttribute("error", "No file was uploaded");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            
            String fileName = getFileName(filePart);
            if (fileName == null || fileName.isEmpty()) {
                request.getSession().setAttribute("error", "Invalid file name");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            
            // Generate unique filename to prevent overwriting
            String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Delete old profile picture if exists
            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                File oldFile = new File(uploadPath + File.separator + user.getProfilePicture());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            
            // Save the new file
            filePart.write(uploadPath + File.separator + newFileName);
            
            // Update user record in database
            user.setProfilePicture(newFileName);
            boolean success = userDAO.updateProfilePicture(user.getId(), newFileName);
            
            if (success) {
                // Update session
                request.getSession().setAttribute("user", user);
                request.getSession().setAttribute("message", "Profile picture updated successfully");
            } else {
                request.getSession().setAttribute("error", "Failed to update profile picture");
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error uploading profile picture", e);
            request.getSession().setAttribute("error", "Error uploading profile picture: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
    
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
