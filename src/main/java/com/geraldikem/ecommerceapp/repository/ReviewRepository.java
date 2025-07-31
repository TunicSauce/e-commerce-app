package com.geraldikem.ecommerceapp.repository;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.Review;
import com.geraldikem.ecommerceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find all reviews for a specific product, ordered by creation date (newest first)
    List<Review> findByProductOrderByCreatedAtDesc(Product product);
    
    // Find all reviews by a specific user
    List<Review> findByUserOrderByCreatedAtDesc(User user);
    
    // Check if a user has already reviewed a specific product
    Optional<Review> findByUserAndProduct(User user, Product product);
    
    // Check if a user has already reviewed a specific product (boolean check)
    boolean existsByUserAndProduct(User user, Product product);
    
    // Calculate average rating for a product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product = :product")
    Double findAverageRatingByProduct(@Param("product") Product product);
    
    // Count total reviews for a product
    long countByProduct(Product product);
    
    // Find all reviews (for admin panel)
    List<Review> findAllByOrderByCreatedAtDesc();
    
    // Find reviews by rating for a product
    List<Review> findByProductAndRatingOrderByCreatedAtDesc(Product product, int rating);
}