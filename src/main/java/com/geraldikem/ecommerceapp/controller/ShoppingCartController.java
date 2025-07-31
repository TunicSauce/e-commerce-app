package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.dto.ShoppingCartDto;
import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.service.ProductService;
import com.geraldikem.ecommerceapp.service.ShoppingCartService;
import com.geraldikem.ecommerceapp.service.UserService;
import com.geraldikem.ecommerceapp.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final WishlistService wishlistService;
    private final UserService userService;
    private final ProductService productService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, 
                                 WishlistService wishlistService,
                                 UserService userService,
                                 ProductService productService) {
        this.shoppingCartService = shoppingCartService;
        this.wishlistService = wishlistService;
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping
    public String showCart(Model model, Authentication authentication, HttpSession session) {
        String username = (authentication != null) ? authentication.getName() : null;
        ShoppingCartDto cartDto = shoppingCartService.getCartForUser(username, session);
        model.addAttribute("cart", cartDto);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity,
                            Authentication authentication,
                            HttpSession session,
                            RedirectAttributes redirectAttributes, // Add this
                            HttpServletRequest request) {
        String username = (authentication != null) ? authentication.getName() : null;
        shoppingCartService.addProductToCart(username, productId, quantity, session);


        redirectAttributes.addFlashAttribute("cartMessage", "Item added to cart!");

        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") Long productId,
                                 Authentication authentication,
                                 HttpSession session) {
        String username = (authentication != null) ? authentication.getName() : null;
        shoppingCartService.removeProductFromCart(username, productId, session);
        return "redirect:/cart";
    }

    @PostMapping("/apply-discount")
    public String applyDiscount(@RequestParam String code, HttpSession session, RedirectAttributes redirectAttributes) {
        boolean isApplied = shoppingCartService.applyDiscountCode(session, code);
        if (isApplied) {
            redirectAttributes.addFlashAttribute("successMessage", "Discount code applied successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or inactive discount code.");
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove-discount")
    public String removeDiscount(HttpSession session, RedirectAttributes redirectAttributes) {
        shoppingCartService.removeDiscountCode(session);
        redirectAttributes.addFlashAttribute("successMessage", "Discount removed.");
        return "redirect:/cart";
    }

    @PostMapping("/save-for-later/{productId}")
    public String saveForLater(@PathVariable("productId") Long productId,
                              Authentication authentication,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login to save items for later.");
            return "redirect:/login";
        }

        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            Product product = productService.findProductById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            wishlistService.addToWishlist(user, product);

            shoppingCartService.removeProductFromCart(authentication.getName(), productId, session);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Item saved for later! You can find it in your wishlist.");
                    
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to save item for later. Please try again.");
        }

        return "redirect:/cart";
    }
}