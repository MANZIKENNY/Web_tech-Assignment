package com.auca.ems.controller;

import com.auca.ems.model.User;
import com.auca.ems.service.RequestCounterService;
import com.auca.ems.service.SessionTrackingService;
import com.auca.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SessionTrackingService sessionTrackingService;
    
    @Autowired
    private RequestCounterService requestCounterService;
    
    @GetMapping
    public String adminDashboard(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getRole() != User.UserRole.ADMIN) {
            return "redirect:/access-denied";
        }
        
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        
        // Add stats info
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalEmployees", users.stream()
            .filter(u -> u.getRole() == User.UserRole.EMPLOYEE).count());
        model.addAttribute("totalAdmins", users.stream()
            .filter(u -> u.getRole() == User.UserRole.ADMIN).count());
        model.addAttribute("totalHRs", users.stream()
            .filter(u -> u.getRole() == User.UserRole.HR).count());
            
        // Add active users info
        Map<String, SessionTrackingService.UserSession> activeSessions = sessionTrackingService.getActiveSessions();
        model.addAttribute("activeSessions", activeSessions);
        model.addAttribute("activeUsers", sessionTrackingService.getActiveSessionCount());
            
        // Add request stats
        model.addAttribute("totalRequests", requestCounterService.getTotalRequests());
        model.addAttribute("recentRequests", requestCounterService.getRequestsInLastMinute());
            
        return "admin/dashboard";
    }
    
    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", User.UserRole.values());
        return "admin/edit-user";
    }
    
    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable Long id, @RequestParam User.UserRole role,
                            RedirectAttributes redirectAttributes) {
        User user = userService.findById(id);
        user.setRole(role);
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "User role updated successfully!");
        return "redirect:/admin";
    }
    
    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/admin";
    }
}
