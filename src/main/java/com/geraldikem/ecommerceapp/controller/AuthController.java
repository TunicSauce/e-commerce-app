package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.dto.UserRegistrationDto;
import com.geraldikem.ecommerceapp.service.PasswordValidationService;
import com.geraldikem.ecommerceapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordValidationService passwordValidationService;

    public AuthController(UserService userService, PasswordValidationService passwordValidationService) {
        this.userService = userService;
        this.passwordValidationService = passwordValidationService;
    }

    /**
     * This is the new endpoint that Spring Security will redirect to.
     * It checks where the user came from and redirects them again with a message if needed.
     */
    @GetMapping("/auth/login")
    public String customLogin(HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        if (referrer != null && referrer.contains("/checkout")) {
            return "redirect:/login?message=checkout";
        }

        return "redirect:/login";
    }


    /**
     * This method displays the actual login page.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        try {
            PasswordValidationService.PasswordValidationResult validationResult = 
                passwordValidationService.validatePassword(registrationDto.getPassword());
            
            if (!validationResult.isValid()) {
                model.addAttribute("user", registrationDto);
                model.addAttribute("passwordErrors", validationResult.getErrors());
                return "register";
            }
            
            userService.save(registrationDto);
            redirectAttributes.addFlashAttribute("success", 
                "Registration successful! You can now log in with your credentials.");
            return "redirect:/register?success";
            
        } catch (Exception e) {
            model.addAttribute("user", registrationDto);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}