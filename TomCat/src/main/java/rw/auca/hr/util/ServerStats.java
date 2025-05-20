package rw.auca.hr.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ServerStats implements ServletRequestListener {
    private static final AtomicInteger activeSessions = new AtomicInteger(0);
    private static final AtomicInteger peakActiveSessions = new AtomicInteger(0);
    private static final AtomicLong totalRequests = new AtomicLong(0);
    private static final LongAdder totalSessionDuration = new LongAdder();
    private static final ConcurrentHashMap<String, Long> sessionStartTimes = new ConcurrentHashMap<>();
    
    // Static resource patterns
    private static final String[] STATIC_PATTERNS = {
        ".css", ".js", ".jpg", ".png", ".svg", ".ico",
        "/css/", "/js/", "/assets/", "/images/"
    };
    
    // Request attribute to prevent duplicate counting
    private static final String REQUEST_PROCESSED = "request_processed";

    // To track unique sessions
    private static final ConcurrentHashMap<String, Boolean> activeSessionIds = new ConcurrentHashMap<>();
    
    public static void sessionCreated(String sessionId) {
        if (activeSessionIds.putIfAbsent(sessionId, true) == null) {
            activeSessions.incrementAndGet();
            peakActiveSessions.updateAndGet(x -> Math.max(x, activeSessions.get()));
            sessionStartTimes.put(sessionId, System.currentTimeMillis());
            System.out.println("Session created: " + sessionId);
        }
        logActiveSessions();
    }
    
    public static void sessionDestroyed(String sessionId) {
        if (activeSessionIds.remove(sessionId) != null) {
            activeSessions.decrementAndGet();
            Long startTime = sessionStartTimes.remove(sessionId);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                totalSessionDuration.add(duration);
            }
            System.out.println("Session destroyed: " + sessionId);
        }
        logActiveSessions();
    }
    
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        if (sre.getServletRequest() instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
            
            // Skip static resources and already counted requests
            if (isStaticResource(request.getRequestURI()) || 
                request.getAttribute(REQUEST_PROCESSED) != null) {
                return;
            }
            
            // Mark this request as processed
            request.setAttribute(REQUEST_PROCESSED, Boolean.TRUE);
            totalRequests.incrementAndGet();
            System.out.println("Request initialized: " + request.getRequestURI());
            logTotalRequests();
        }
    }
    
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // Nothing to do
    }
    
    private static boolean isStaticResource(String uri) {
        if (uri == null) return false;
        
        for (String pattern : STATIC_PATTERNS) {
            if (uri.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    public static int getActiveSessions() {
        return activeSessions.get();
    }
    
    public static long getTotalRequests() {
        return totalRequests.get();
    }
    
    public static long getPeakActiveSessions() {
        return peakActiveSessions.get();
    }
    
    public static double getAverageSessionDuration() {
        long sessionCount = totalRequests.get() > 0 ? totalRequests.get() : 1; // Avoid division by zero
        return totalSessionDuration.sum() / (double) sessionCount;
    }

    private static void logActiveSessions() {
        System.out.println("Active sessions count: " + activeSessions.get());
    }

    private static void logTotalRequests() {
        System.out.println("Total requests count: " + totalRequests.get());
    }
}
