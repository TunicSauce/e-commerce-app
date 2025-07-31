package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.dto.ShoppingCartDto;
import jakarta.servlet.http.HttpSession;

public interface ShoppingCartService {
    void addProductToCart(String userEmail, Long productId, int quantity, HttpSession session);
    ShoppingCartDto getCartForUser(String userEmail, HttpSession session);
    void removeProductFromCart(String userEmail, Long productId, HttpSession session);
    void updateProductQuantity(String userEmail, Long productId, int quantity, HttpSession session);
    void mergeSessionCartWithUserCart(HttpSession session, String userEmail);
    boolean applyDiscountCode(HttpSession session, String code);
    void removeDiscountCode(HttpSession session);
}