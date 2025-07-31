package com.geraldikem.ecommerceapp.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Check if the request was for the checkout page
        if ("/checkout".equals(request.getRequestURI())) {
            // Redirect to the login page with a specific message parameter
            response.sendRedirect(request.getContextPath() + "/login?message=checkout");
        } else {
            // Default behavior for other protected pages
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}