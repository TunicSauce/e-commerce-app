package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.service.ProductService;
import com.geraldikem.ecommerceapp.service.UserService;
import com.geraldikem.ecommerceapp.service.WishlistService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProductService productService;
    private final WishlistService wishlistService;
    private final UserService userService;

    public HomeController(ProductService productService, WishlistService wishlistService, UserService userService) {
        this.productService = productService;
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        List<Product> featuredProducts = productService.findFeaturedProducts();
        List<Product> newArrivals = productService.findNewestProducts();
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("newArrivals", newArrivals);

        Set<Long> wishlistProductIds = getWishlistProductIds(authentication);
        model.addAttribute("wishlistProductIds", wishlistProductIds);

        return "index";
    }

    private Set<Long> getWishlistProductIds(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByEmail(authentication.getName()).orElse(null);
            if (user != null) {
                return wishlistService.getWishlistForUser(user)
                        .stream()
                        .map(Product::getId)
                        .collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }
}