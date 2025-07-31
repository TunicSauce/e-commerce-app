package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.dto.ShoppingCartDto;
import com.geraldikem.ecommerceapp.model.Order;
import com.geraldikem.ecommerceapp.service.EmailService;
import com.geraldikem.ecommerceapp.service.OrderService;
import com.geraldikem.ecommerceapp.service.PaymentService;
import com.geraldikem.ecommerceapp.service.ShoppingCartService;
import com.stripe.model.PaymentIntent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/checkout")
@Slf4j
public class CheckoutController {

    private final ShoppingCartService shoppingCartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final EmailService emailService;
    
    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    public CheckoutController(ShoppingCartService shoppingCartService, 
                            OrderService orderService,
                            PaymentService paymentService,
                            EmailService emailService) {
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    @GetMapping
    public String checkoutPage(Model model, Authentication authentication, HttpSession session) {

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return "redirect:/login?message=checkout";
        }


        String userEmail = authentication.getName();
        ShoppingCartDto cart = shoppingCartService.getCartForUser(userEmail, session);
        model.addAttribute("cart", cart);
        model.addAttribute("stripePublishableKey", stripePublishableKey);
        return "checkout";
    }

    @PostMapping
    public String placeOrder(Authentication authentication,
                             @RequestParam String shippingAddress,
                             @RequestParam String shippingCity,
                             @RequestParam String shippingProvince,
                             @RequestParam String shippingPostalCode,
                             @RequestParam(required = false) String paymentIntentId,
                             HttpSession session,
                             Model model) {

        try {
            // Get the logged-in user's email
            String userEmail = authentication.getName();
            
            // Check if paymentIntentId is provided
            if (paymentIntentId == null || paymentIntentId.trim().isEmpty()) {
                log.error("PaymentIntentId is missing for user: {}", userEmail);
                ShoppingCartDto cart = shoppingCartService.getCartForUser(userEmail, session);
                model.addAttribute("cart", cart);
                model.addAttribute("stripePublishableKey", stripePublishableKey);
                model.addAttribute("error", "Payment information is missing. Please complete the payment process.");
                return "checkout";
            }
            
            // Confirm the payment with Stripe
            PaymentIntent confirmedPayment = paymentService.confirmPaymentIntent(paymentIntentId);
            
            if (!"succeeded".equals(confirmedPayment.getStatus())) {
                ShoppingCartDto cart = shoppingCartService.getCartForUser(userEmail, session);
                model.addAttribute("cart", cart);
                model.addAttribute("stripePublishableKey", stripePublishableKey);
                model.addAttribute("error", "Payment failed. Please try again.");
                return "checkout";
            }

            // Call the service to create the order
            Order order = orderService.createOrder(userEmail, shippingAddress, shippingCity, shippingProvince, shippingPostalCode);
            
            // Update order status to processing
            orderService.updateOrderStatus(order.getId(), "PROCESSING");
            
            // Send confirmation email
            emailService.sendOrderConfirmationEmail(order);
            
            log.info("Order created successfully: {} for user: {}", order.getId(), userEmail);

            // Redirect to a confirmation page after the order is placed
            return "redirect:/checkout/order-confirmation";
            
        } catch (Exception e) {
            log.error("Error processing order", e);
            model.addAttribute("error", "An error occurred while processing your order. Please try again.");
            return "checkout";
        }
    }

    @GetMapping("/order-confirmation")
    public String orderConfirmation() {
        return "order-confirmation";
    }
}