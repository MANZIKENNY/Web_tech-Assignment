package com.auca.ems.config;

import com.auca.ems.model.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // Allow access to login page, static resources and error pages
        if (requestURI.contains("/login") || 
            requestURI.contains("/register") || 
            requestURI.contains("/css/") || 
            requestURI.contains("/js/") || 
            requestURI.contains("/assets/") ||
            requestURI.contains("/error")) {
            return true;
        }
        
        // Check if user is logged in
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        // Role-based access control
        if (requestURI.contains("/admin") && user.getRole() != User.UserRole.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return false;
        }
        
        if ((requestURI.contains("/employees/add") || 
             requestURI.contains("/employees/edit") || 
             requestURI.contains("/employees/delete")) && 
            (user.getRole() != User.UserRole.HR && user.getRole() != User.UserRole.ADMIN)) {
            response.sendRedirect(request.getContextPath() + "/access-denied");
            return false;
        }
        
        return true;
    }
}
