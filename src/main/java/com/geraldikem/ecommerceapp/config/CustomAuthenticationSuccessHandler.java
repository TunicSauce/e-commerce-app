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

        userRepository.findByEmail(userEmail).ifPresent(user -> {
            String firstName = user.getFirstName();
            session.setAttribute("userFirstName", firstName);
        });

        shoppingCartService.mergeSessionCartWithUserCart(session, userEmail);
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            response.sendRedirect("/admin/dashboard");
            return;
        }

        response.sendRedirect("/");
    }
}