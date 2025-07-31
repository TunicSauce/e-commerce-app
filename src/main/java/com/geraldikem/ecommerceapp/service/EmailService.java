package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Order;
import com.geraldikem.ecommerceapp.model.User;

public interface EmailService {
    void sendOrderConfirmationEmail(Order order);
    void sendOrderStatusUpdateEmail(Order order, String status);
    void sendWelcomeEmail(User user);
}