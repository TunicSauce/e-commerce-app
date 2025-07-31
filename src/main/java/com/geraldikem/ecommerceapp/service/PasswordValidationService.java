package com.geraldikem.ecommerceapp.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordValidationService {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");
    
    // Common weak passwords to reject
    private static final String[] COMMON_PASSWORDS = {
        "password", "123456", "123456789", "12345678", "12345", "1234567", "1234567890",
        "qwerty", "abc123", "password123", "admin", "letmein", "welcome", "monkey",
        "dragon", "master", "github", "jordan", "harley", "ranger", "shadow", "superman"
    };

    public PasswordValidationResult validatePassword(String password) {
        PasswordValidationResult result = new PasswordValidationResult();
        
        if (password == null) {
            result.addError("Password cannot be null");
            return result;
        }

        if (password.length() < MIN_LENGTH) {
            result.addError("Password must be at least " + MIN_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_LENGTH) {
            result.addError("Password must not exceed " + MAX_LENGTH + " characters");
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            result.addError("Password must contain at least one uppercase letter");
        }
        
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            result.addError("Password must contain at least one lowercase letter");
        }
        
        if (!DIGIT_PATTERN.matcher(password).find()) {
            result.addError("Password must contain at least one number");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            result.addError("Password must contain at least one special character (!@#$%^&*()_+-=[]{}';\":\\|,.<>/?)");
        }

        String lowerPassword = password.toLowerCase();
        for (String commonPassword : COMMON_PASSWORDS) {
            if (lowerPassword.contains(commonPassword)) {
                result.addError("Password contains common patterns and is not secure");
                break;
            }
        }

        if (hasRepeatedCharacters(password, 4)) {
            result.addError("Password cannot contain more than 3 identical characters in a row");
        }

        if (hasSequentialCharacters(password)) {
            result.addError("Password cannot contain sequential characters (like 'abcd' or '1234')");
        }
        
        return result;
    }
    
    private boolean hasRepeatedCharacters(String password, int maxRepeats) {
        for (int i = 0; i <= password.length() - maxRepeats; i++) {
            char currentChar = password.charAt(i);
            int count = 1;
            
            for (int j = i + 1; j < password.length() && password.charAt(j) == currentChar; j++) {
                count++;
                if (count >= maxRepeats) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean hasSequentialCharacters(String password) {
        for (int i = 0; i <= password.length() - 4; i++) {
            String substring = password.substring(i, i + 4);

            boolean isAscending = true;
            for (int j = 1; j < 4; j++) {
                if (substring.charAt(j) != substring.charAt(j - 1) + 1) {
                    isAscending = false;
                    break;
                }
            }

            boolean isDescending = true;
            for (int j = 1; j < 4; j++) {
                if (substring.charAt(j) != substring.charAt(j - 1) - 1) {
                    isDescending = false;
                    break;
                }
            }
            
            if (isAscending || isDescending) {
                return true;
            }
        }
        return false;
    }
    
    public static class PasswordValidationResult {
        private final List<String> errors = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
        
        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}