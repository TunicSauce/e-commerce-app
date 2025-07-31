package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.User;
import java.util.Set;

public interface WishlistService {
    void addToWishlist(User user, Product product);
    void removeFromWishlist(User user, Product product);
    Set<Product> getWishlistForUser(User user);
}