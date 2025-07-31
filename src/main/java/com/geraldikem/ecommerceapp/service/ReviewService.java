package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.Review;
import com.geraldikem.ecommerceapp.model.User;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    
    // Check if a user is eligible to review a product (must have purchased it)
    boolean canUserReviewProduct(User user, Product product);
    
    // Save a new review (with eligibility check)
    Review saveReview(User user, Product product, int rating, String comment) throws IllegalStateException;
    
    // Get all reviews for a product
    List<Review> getReviewsForProduct(Product product);
    
    // Get average rating for a product
    Double getAverageRating(Product product);
    
    // Get total number of reviews for a product
    long getReviewCount(Product product);
    
    // Check if user has already reviewed a product
    boolean hasUserReviewedProduct(User user, Product product);
    
    // Get user's review for a product (if exists)
    Optional<Review> getUserReviewForProduct(User user, Product product);
    
    // Get all reviews by a user
    List<Review> getReviewsByUser(User user);
    
    // Get all reviews (for admin)
    List<Review> getAllReviews();
    
    // Delete a review (for admin moderation)
    void deleteReview(Long reviewId);
}