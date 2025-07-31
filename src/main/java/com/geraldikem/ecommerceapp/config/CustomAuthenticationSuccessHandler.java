package com.geraldikem.ecommerceapp.config;

import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.repository.UserRepository;
import com.geraldikem.ecommerceapp.service.ShoppingCartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ShoppingCartService shoppingCartService;
    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(ShoppingCartService shoppingCartService, UserRepository userRepository) {
        this.shoppingCartService = shoppingCartService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userEmail = authentication.getName();

        // Find the user to get their first name and put it in the session
        userRepository.findByEmail(userEmail).ifPresent(user -> {
            String firstName = user.getFirstName();
            session.setAttribute("userFirstName", firstName);
        });

        // Merge the session cart with the user's database cart
        shoppingCartService.mergeSessionCartWithUserCart(session, userEmail);

        // Check if user is admin and redirect accordingly
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            response.sendRedirect("/admin/dashboard");
            return;
        }

        // For regular users, redirect to home page or continue with default behavior
        response.sendRedirect("/");
    }
}