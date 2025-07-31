package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Order;

import java.math.BigDecimal;
import java.util.List; // Import List
import java.util.Optional;

public interface OrderService {
    Order createOrder(String userEmail, String shippingAddress, String shippingCity, String shippingProvince, String shippingPostalCode);
    List<Order> findOrdersByUser(String userEmail);
    BigDecimal getTotalRevenue();
    long getTotalOrdersCount();
    List<Order> getRecentOrders();
    void updateOrderStatus(Long orderId, String status);
    Optional<Order> findOrderById(Long orderId);
    List<Order> findAllOrders();
}