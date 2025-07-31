package com.geraldikem.ecommerceapp.controller;

import com.geraldikem.ecommerceapp.dto.ShoppingCartDto;
import com.geraldikem.ecommerceapp.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final ShoppingCartService shoppingCartService;

    public CartApiController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping("/update")
    public ResponseEntity<ShoppingCartDto> updateCartQuantity(@RequestBody UpdateCartRequest request,
                                                              Authentication authentication,
                                                              HttpSession session) {
        String username = (authentication != null) ? authentication.getName() : null;
        shoppingCartService.updateProductQuantity(username, request.getProductId(), request.getQuantity(), session);
        ShoppingCartDto updatedCart = shoppingCartService.getCartForUser(username, session);
        return ResponseEntity.ok(updatedCart);
    }
}

@Data
class UpdateCartRequest {
    private Long productId;
    private int quantity;
}