package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.User;
import com.geraldikem.ecommerceapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final UserRepository userRepository;

    public WishlistServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void addToWishlist(User user, Product product) {
        user.getWishlist().add(product);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeFromWishlist(User user, Product product) {
        user.getWishlist().remove(product);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Product> getWishlistForUser(User user) {
        // By fetching the user again, we ensure the wishlist collection is loaded
        return userRepository.findById(user.getId()).map(User::getWishlist).orElse(Set.of());
    }
}