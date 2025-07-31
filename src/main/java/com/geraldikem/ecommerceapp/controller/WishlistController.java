package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.service.ProductService;
import com.geraldikem.ecommerceapp.service.UserService;
import com.geraldikem.ecommerceapp.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;
    private final ProductService productService;

    public WishlistController(WishlistService wishlistService, UserService userService, ProductService productService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
        this.productService = productService;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }

    @GetMapping
    public String showWishlist(Model model, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        Set<Product> wishlist = wishlistService.getWishlistForUser(user);
        model.addAttribute("wishlist", wishlist);
        return "wishlist";
    }

    @GetMapping("/add/{productId}")
    public String addToWishlist(@PathVariable Long productId, Authentication authentication, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(authentication);
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + productId));
        wishlistService.addToWishlist(user, product);

        redirectAttributes.addFlashAttribute("wishlistMessage", "Item added to wishlist!");
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/remove/{productId}")
    public String removeFromWishlist(@PathVariable Long productId, Authentication authentication, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(authentication);
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + productId));
        wishlistService.removeFromWishlist(user, product);

        redirectAttributes.addFlashAttribute("wishlistMessage", "Item removed from wishlist!");
        return "redirect:" + request.getHeader("Referer");
    }
}