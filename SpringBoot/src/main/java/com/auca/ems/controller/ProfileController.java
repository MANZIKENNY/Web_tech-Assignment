package com.auca.ems.controller;

import com.auca.ems.model.User;
import com.auca.ems.service.FileStorageService;
import com.auca.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        return "profile/view";
    }
    
    @GetMapping("/edit")
    public String editProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get the most up-to-date user data
        User user = userService.findById(currentUser.getId());
        model.addAttribute("user", user);
        return "profile/edit";
    }
    
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            @RequestParam(required = false) MultipartFile profileImage,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get fresh data from DB
        User user = userService.findById(currentUser.getId());
        
        try {
            boolean changed = false;
            
            // Update username if provided and different
            if (StringUtils.hasText(username) && !username.equals(user.getUsername())) {
                // Check if username already exists
                User existingUser = userService.findByUsername(username);
                if (existingUser != null && !Objects.equals(existingUser.getId(), user.getId())) {
                    redirectAttributes.addFlashAttribute("error", "Username already taken");
                    return "redirect:/profile/edit";
                }
                user.setUsername(username);
                changed = true;
            }
            
            // Handle password change
            if (StringUtils.hasText(currentPassword) && StringUtils.hasText(newPassword)) {
                // Validate current password
                if (!currentPassword.equals(user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
                    return "redirect:/profile/edit";
                }
                
                // Confirm passwords match
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "New passwords do not match");
                    return "redirect:/profile/edit";
                }
                
                user.setPassword(newPassword);
                changed = true;
            }
            
            // Handle profile image
            if (profileImage != null && !profileImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(profileImage);
                user.setProfilePicture(fileName);
                changed = true;
            }
            
            if (changed) {
                userService.save(user);
                // Update session with new data
                session.setAttribute("user", user);
                redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
            }
            
            return "redirect:/profile";
            
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }
}
