package com.auca.ems.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RequestCounterService {
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger currentMinuteRequests = new AtomicInteger(0);
    private final Queue<LocalDateTime> recentRequests = new LinkedList<>();
    private static final int RECENT_REQUESTS_WINDOW = 60; // seconds
    
    public void incrementRequests() {
        totalRequests.incrementAndGet();
        currentMinuteRequests.incrementAndGet();
        
        // Add to recent requests
        synchronized (recentRequests) {
            LocalDateTime now = LocalDateTime.now();
            recentRequests.add(now);
            
            // Clean old requests (older than RECENT_REQUESTS_WINDOW seconds)
            while (!recentRequests.isEmpty()) {
                LocalDateTime oldest = recentRequests.peek();
                if (oldest.plusSeconds(RECENT_REQUESTS_WINDOW).isBefore(now)) {
                    recentRequests.poll();
                } else {
                    break;
                }
            }
        }
    }
    
    public int getTotalRequests() {
        return totalRequests.get();
    }
    
    public int getRequestsInLastMinute() {
        synchronized (recentRequests) {
            // Clean old requests first
            LocalDateTime cutoff = LocalDateTime.now().minusSeconds(RECENT_REQUESTS_WINDOW);
            while (!recentRequests.isEmpty() && recentRequests.peek().isBefore(cutoff)) {
                recentRequests.poll();
            }
            return recentRequests.size();
        }
    }
    
    public void resetMinuteCounter() {
        currentMinuteRequests.set(0);
    }
}
