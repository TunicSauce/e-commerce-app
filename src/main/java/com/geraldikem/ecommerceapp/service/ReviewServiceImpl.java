package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.*;
import com.geraldikem.ecommerceapp.repository.OrderRepository;
import com.geraldikem.ecommerceapp.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    @Override
    public boolean canUserReviewProduct(User user, Product product) {
        if (user == null || product == null) {
            return false;
        }

        List<Order> userOrders = orderRepository.findByUserOrderByOrderDateDesc(user);
        
        for (Order order : userOrders) {
            if (order.getStatus().equalsIgnoreCase("DELIVERED") || 
                order.getStatus().equalsIgnoreCase("PROCESSING")) {
                
                for (OrderItem item : order.getOrderItems()) {
                    if (item.getProduct().getId().equals(product.getId())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    @Transactional
    public Review saveReview(User user, Product product, int rating, String comment) throws IllegalStateException {

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        

        if (!canUserReviewProduct(user, product)) {
            throw new IllegalStateException("User is not eligible to review this product. Must have purchased it first.");
        }
        

        if (hasUserReviewedProduct(user, product)) {
            throw new IllegalStateException("User has already reviewed this product");
        }

        Review review = new Review(user, product, rating, comment);
        Review savedReview = reviewRepository.save(review);
        
        log.info("Review saved: User {} reviewed Product {} with rating {}", 
                user.getEmail(), product.getName(), rating);
        
        return savedReview;
    }

    @Override
    public List<Review> getReviewsForProduct(Product product) {
        return reviewRepository.findByProductOrderByCreatedAtDesc(product);
    }

    @Override
    public Double getAverageRating(Product product) {
        Double average = reviewRepository.findAverageRatingByProduct(product);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0; // Round to 1 decimal place
    }

    @Override
    public long getReviewCount(Product product) {
        return reviewRepository.countByProduct(product);
    }

    @Override
    public boolean hasUserReviewedProduct(User user, Product product) {
        return reviewRepository.existsByUserAndProduct(user, product);
    }

    @Override
    public Optional<Review> getUserReviewForProduct(User user, Product product) {
        return reviewRepository.findByUserAndProduct(user, product);
    }

    @Override
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
        log.info("Review deleted: ID {}", reviewId);
    }
}