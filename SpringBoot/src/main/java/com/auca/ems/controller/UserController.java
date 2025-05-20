package com.auca.ems.controller;

import com.auca.ems.model.User;
import com.auca.ems.service.FileStorageService;
import com.auca.ems.service.SessionTrackingService;
import com.auca.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private SessionTrackingService sessionTrackingService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, 
                      HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(username);
        
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            
            // Register active session
            sessionTrackingService.registerSession(session.getId(), user);
            
            // Redirect based on role
            if (user.getRole() == User.UserRole.ADMIN) {
                return "redirect:/admin";
            } else {
                return "redirect:/employees";
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, 
                         @RequestParam(required = false) MultipartFile profileImage,
                         RedirectAttributes redirectAttributes) {
        try {
            if (userService.findByUsername(user.getUsername()) != null) {
                redirectAttributes.addFlashAttribute("error", "Username already taken");
                return "redirect:/register";
            }
            
            // Default role for new users
            user.setRole(User.UserRole.EMPLOYEE);
            
            // Handle profile image if provided
            if (profileImage != null && !profileImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(profileImage);
                user.setProfilePicture(fileName);
            }
            
            userService.save(user);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload profile picture");
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Remove active session
        sessionTrackingService.removeSession(session.getId());
        session.invalidate();
        return "redirect:/login";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
