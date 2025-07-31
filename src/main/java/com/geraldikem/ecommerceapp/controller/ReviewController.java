package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.Review;
import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.service.InputSanitizationService;
import com.geraldikem.ecommerceapp.service.ProductService;
import com.geraldikem.ecommerceapp.service.RateLimitService;
import com.geraldikem.ecommerceapp.service.ReviewService;
import com.geraldikem.ecommerceapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final UserService userService;
    private final RateLimitService rateLimitService;
    private final InputSanitizationService inputSanitizationService;

    @PostMapping("/add")
    public String addReview(@RequestParam Long productId,
                           @RequestParam int rating,
                           @RequestParam String comment,
                           Authentication authentication,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        
        try {
            // Check if user is authenticated
            if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
                redirectAttributes.addFlashAttribute("error", "You must be logged in to leave a review.");
                return "redirect:/products/" + productId;
            }

            // Get current user
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Check rate limiting - both by IP and by user
            String clientIp = getClientIpAddress(request);
            String userEmail = user.getEmail();
            
            if (!rateLimitService.isAllowed(clientIp, RateLimitService.RateLimitType.REVIEW_BY_IP)) {
                log.warn("Rate limit exceeded for IP: {}", clientIp);
                redirectAttributes.addFlashAttribute("error", 
                    "Too many reviews submitted from this location. Please try again later.");
                return "redirect:/products/" + productId;
            }
            
            if (!rateLimitService.isAllowed(userEmail, RateLimitService.RateLimitType.REVIEW_BY_USER)) {
                log.warn("Rate limit exceeded for user: {}", userEmail);
                redirectAttributes.addFlashAttribute("error", 
                    "You have submitted too many reviews recently. Please try again later.");
                return "redirect:/products/" + productId;
            }

            // Get product
            Product product = productService.findProductById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            // Validate rating
            if (rating < 1 || rating > 5) {
                redirectAttributes.addFlashAttribute("error", "Rating must be between 1 and 5 stars.");
                return "redirect:/products/" + productId;
            }

            // Sanitize and validate comment
            String sanitizedComment = inputSanitizationService.sanitizeUserInput(comment);
            if (sanitizedComment == null || sanitizedComment.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please provide a review comment.");
                return "redirect:/products/" + productId;
            }

            // Save review with sanitized comment
            Review savedReview = reviewService.saveReview(user, product, rating, sanitizedComment);
            
            log.info("Review added successfully: User {} reviewed Product {} with rating {}", 
                    user.getEmail(), product.getName(), rating);
            
            redirectAttributes.addFlashAttribute("success", 
                    "Thank you for your review! Your feedback helps other customers make informed decisions.");

        } catch (IllegalStateException e) {
            log.warn("Review submission failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error adding review", e);
            redirectAttributes.addFlashAttribute("error", 
                    "An error occurred while submitting your review. Please try again.");
        }

        return "redirect:/products/" + productId;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}