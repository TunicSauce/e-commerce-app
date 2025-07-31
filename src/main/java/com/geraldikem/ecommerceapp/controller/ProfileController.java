package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.model.Order;
import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.service.OrderService;
import com.geraldikem.ecommerceapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final OrderService orderService;

    public ProfileController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public String showProfilePage(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Fetch the user's order history
        List<Order> orders = orderService.findOrdersByUser(email);

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                Authentication authentication,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        userService.updateProfile(email, firstName, lastName);

        // Updates the name in the session so the navbar changes
        session.setAttribute("userFirstName", firstName);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }
}