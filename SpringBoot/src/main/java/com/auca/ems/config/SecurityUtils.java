package com.auca.ems.config;

import com.auca.ems.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class SecurityUtils {

    // Get current logged in user with high performance
    public static Optional<User> getCurrentUser() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            return Optional.empty();
        }
        
        HttpSession session = attr.getRequest().getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        
        return Optional.ofNullable((User) session.getAttribute("user"));
    }
    
    // Get formatted current UTC time
    public static String getCurrentUtcTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    // Get client IP address (useful for logging)
    public static String getClientIp() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            return "unknown";
        }
        
        HttpServletRequest request = attr.getRequest();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        return request.getRemoteAddr();
    }
}
