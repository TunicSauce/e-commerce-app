package com.geraldikem.ecommerceapp.repository;

import com.geraldikem.ecommerceapp.model.ShoppingCart;
import com.geraldikem.ecommerceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}