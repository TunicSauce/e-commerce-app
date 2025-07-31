package com.geraldikem.ecommerceapp.repository;

import com.geraldikem.ecommerceapp.model.Order;
import com.geraldikem.ecommerceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByOrderDateDesc(User user);

    // Custom query to calculate the sum of the totalAmount for all orders
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal findTotalRevenue();

    // Finds the top 5 most recent orders
    List<Order> findTop5ByOrderByOrderDateDesc();
    
    // Finds all orders ordered by date descending
    List<Order> findAllByOrderByOrderDateDesc();
}