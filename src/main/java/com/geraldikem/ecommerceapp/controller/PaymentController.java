package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.dto.ShoppingCartDto;
import com.geraldikem.ecommerceapp.service.PaymentService;
import com.geraldikem.ecommerceapp.service.ShoppingCartService;
import com.stripe.model.PaymentIntent;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final ShoppingCartService shoppingCartService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(
            Authentication authentication, 
            HttpSession session) {
        
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not authenticated"));
            }

            String userEmail = authentication.getName();
            ShoppingCartDto cart = shoppingCartService.getCartForUser(userEmail, session);
            
            if (cart.getItems().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cart is empty"));
            }

            BigDecimal totalAmount = cart.getGrandTotal();
            
            // Create customer if needed (you might want to store customer ID in User entity)
            // String customerId = paymentService.createCustomer(userEmail, "Customer Name");
            
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(
                totalAmount, 
                "usd", 
                null // customerId
            );

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("paymentIntentId", paymentIntent.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error creating payment intent", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to create payment intent"));
        }
    }
}