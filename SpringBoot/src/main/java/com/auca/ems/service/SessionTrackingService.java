package com.auca.ems.service;

import com.auca.ems.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionTrackingService {
    
    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    
    public void registerSession(String sessionId, User user) {
        activeSessions.put(sessionId, new UserSession(user, LocalDateTime.now()));
    }
    
    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
    
    public Map<String, UserSession> getActiveSessions() {
        return activeSessions;
    }
    
    public int getActiveSessionCount() {
        return activeSessions.size();
    }
    
    public static class UserSession {
        private final User user;
        private final LocalDateTime loginTime;
        
        public UserSession(User user, LocalDateTime loginTime) {
            this.user = user;
            this.loginTime = loginTime;
        }
        
        public User getUser() {
            return user;
        }
        
        public LocalDateTime getLoginTime() {
            return loginTime;
        }
    }
}
