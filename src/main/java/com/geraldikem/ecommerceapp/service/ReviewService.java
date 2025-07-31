package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Product;
import com.geraldikem.ecommerceapp.model.Review;
import com.geraldikem.ecommerceapp.model.User;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    boolean canUserReviewProduct(User user, Product product);
    Review saveReview(User user, Product product, int rating, String comment) throws IllegalStateException;
    List<Review> getReviewsForProduct(Product product);
    Double getAverageRating(Product product);
    long getReviewCount(Product product);
    boolean hasUserReviewedProduct(User user, Product product);
    Optional<Review> getUserReviewForProduct(User user, Product product);
    List<Review> getReviewsByUser(User user);
    List<Review> getAllReviews();
    void deleteReview(Long reviewId);
}