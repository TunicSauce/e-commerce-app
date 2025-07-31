package com.geraldikem.ecommerceapp.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    private static final int MAX_REVIEWS_PER_HOUR = 5;
    private static final int MAX_REVIEWS_PER_DAY = 20;

    public boolean isAllowed(String identifier, RateLimitType type) {
        String key = type + ":" + identifier;
        RateLimitInfo info = rateLimitMap.computeIfAbsent(key, k -> new RateLimitInfo());
        
        LocalDateTime now = LocalDateTime.now();
        
        // Clean up old entries
        if (info.getLastReset().isBefore(now.minusHours(24))) {
            info.reset(now);
        } else if (info.getLastHourReset().isBefore(now.minusHours(1))) {
            info.resetHourly(now);
        }
        
        // Check limits
        if (info.getHourlyCount().get() >= MAX_REVIEWS_PER_HOUR) {
            return false;
        }
        
        if (info.getDailyCount().get() >= MAX_REVIEWS_PER_DAY) {
            return false;
        }
        
        // Increment counters
        info.getHourlyCount().incrementAndGet();
        info.getDailyCount().incrementAndGet();
        
        return true;
    }
    
    public enum RateLimitType {
        REVIEW_BY_IP,
        REVIEW_BY_USER
    }
    
    private static class RateLimitInfo {
        private final AtomicInteger hourlyCount = new AtomicInteger(0);
        private final AtomicInteger dailyCount = new AtomicInteger(0);
        private LocalDateTime lastReset = LocalDateTime.now();
        private LocalDateTime lastHourReset = LocalDateTime.now();
        
        public AtomicInteger getHourlyCount() {
            return hourlyCount;
        }
        
        public AtomicInteger getDailyCount() {
            return dailyCount;
        }
        
        public LocalDateTime getLastReset() {
            return lastReset;
        }
        
        public LocalDateTime getLastHourReset() {
            return lastHourReset;
        }
        
        public void reset(LocalDateTime now) {
            hourlyCount.set(0);
            dailyCount.set(0);
            lastReset = now;
            lastHourReset = now;
        }
        
        public void resetHourly(LocalDateTime now) {
            hourlyCount.set(0);
            lastHourReset = now;
        }
    }
}