package com.auca.ems.config;

import com.auca.ems.service.RequestCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestCounterInterceptor implements HandlerInterceptor {
    
    @Autowired
    private RequestCounterService requestCounterService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Ignore static resources
        String path = request.getRequestURI();
        if (!path.contains("/css/") && !path.contains("/js/") && !path.contains("/assets/") && 
            !path.contains("/uploads/") && !path.contains("/error")) {
            requestCounterService.incrementRequests();
        }
        return true;
    }
}
