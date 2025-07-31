package com.geraldikem.ecommerceapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

@Service
public class InputSanitizationService {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script[^>]*>.*?</script>");
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("(?i)javascript:");
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s");

    public String sanitizeHtml(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // Remove script tags
        String sanitized = SCRIPT_PATTERN.matcher(input).replaceAll("");

        // Remove javascript: protocols
        sanitized = JAVASCRIPT_PATTERN.matcher(sanitized).replaceAll("");
        
        // Escape HTML entities
        sanitized = HtmlUtils.htmlEscape(sanitized);
        
        return sanitized.trim();
    }

    public String sanitizeForDatabase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        // First apply HTML sanitization
        String sanitized = sanitizeHtml(input);

        if (SQL_INJECTION_PATTERN.matcher(sanitized.toLowerCase()).find()) {
            throw new IllegalArgumentException("Input contains potentially dangerous content");
        }
        
        return sanitized;
    }

    public String sanitizeUserInput(String input) {
        if (input == null) {
            return null;
        }

        String sanitized = input.trim();

        sanitized = sanitizeHtml(sanitized);

        if (sanitized.length() > 5000) {
            sanitized = sanitized.substring(0, 5000);
        }
        
        return sanitized;
    }

    public String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return fileName;
        }

        String sanitized = fileName.replaceAll("[./\\\\]", "");

        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._-]", "");

        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }
        
        return sanitized;
    }

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        return emailPattern.matcher(email.trim()).matches() && email.length() <= 254;
    }
}