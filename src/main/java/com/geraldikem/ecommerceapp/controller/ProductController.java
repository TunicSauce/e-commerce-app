package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.Review;
import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.service.ProductService;
import com.geraldikem.ecommerceapp.service.ReviewService;
import com.geraldikem.ecommerceapp.service.UserService;
import com.geraldikem.ecommerceapp.service.WishlistService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final WishlistService wishlistService;
    private final UserService userService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, WishlistService wishlistService, UserService userService, ReviewService reviewService) {
        this.productService = productService;
        this.wishlistService = wishlistService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String listProducts(@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                               @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
                               Model model, Authentication authentication) {
        int pageSize = 6;
        Page<Product> page = productService.searchProducts(keyword, minPrice, maxPrice, pageNo, pageSize);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("products", page.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        Set<Long> wishlistProductIds = getWishlistProductIds(authentication);
        model.addAttribute("wishlistProductIds", wishlistProductIds);

        return "products";
    }

    @GetMapping("/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model, Authentication authentication) {
        Product product = productService.findProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);

        // Get reviews for this product
        List<Review> reviews = reviewService.getReviewsForProduct(product);
        model.addAttribute("reviews", reviews);
        
        // Get review statistics
        Double averageRating = reviewService.getAverageRating(product);
        long reviewCount = reviewService.getReviewCount(product);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("reviewCount", reviewCount);
        
        // Check if current user can review this product
        boolean canReview = false;
        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated() && 
            !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            currentUser = userService.findByEmail(authentication.getName()).orElse(null);
            if (currentUser != null) {
                canReview = reviewService.canUserReviewProduct(currentUser, product) && 
                           !reviewService.hasUserReviewedProduct(currentUser, product);
            }
        }
        model.addAttribute("canReview", canReview);
        model.addAttribute("currentUser", currentUser);

        Set<Long> wishlistProductIds = getWishlistProductIds(authentication);
        model.addAttribute("wishlistProductIds", wishlistProductIds);

        return "product-detail";
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