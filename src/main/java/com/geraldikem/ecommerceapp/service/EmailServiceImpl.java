package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.Order;
import com.geraldikem.ecommerceapp.model.OrderItem;
import com.geraldikem.ecommerceapp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getUser().getEmail());
            message.setSubject("Order Confirmation - Order #" + order.getId());
            message.setText(buildOrderConfirmationMessage(order));
            
            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", order.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email", e);
        }
    }

    @Override
    @Async
    public void sendOrderStatusUpdateEmail(Order order, String status) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getUser().getEmail());
            message.setSubject("Order Status Update - Order #" + order.getId());
            message.setText(buildOrderStatusUpdateMessage(order, status));
            
            mailSender.send(message);
            log.info("Order status update email sent to: {}", order.getUser().getEmail());
        } catch (Exception e) {
            log.error("Failed to send order status update email", e);
        }
    }

    private String buildOrderConfirmationMessage(Order order) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(order.getUser().getFirstName()).append(",\n\n");
        message.append("Thank you for your order! Here are the details:\n\n");
        message.append("Order ID: ").append(order.getId()).append("\n");
        message.append("Order Date: ").append(order.getOrderDate()).append("\n\n");
        message.append("Items Ordered:\n");
        
        for (OrderItem item : order.getOrderItems()) {
            message.append("- ").append(item.getProduct().getName())
                   .append(" (Qty: ").append(item.getQuantity()).append(")")
                   .append(" - $").append(item.getPrice()).append("\n");
        }
        
        message.append("\nShipping Address:\n");
        message.append(order.getShippingAddress()).append("\n");
        message.append(order.getShippingCity()).append(", ");
        message.append(order.getShippingProvince()).append(" ");
        message.append(order.getShippingPostalCode()).append("\n\n");
        message.append("Total Amount: $").append(order.getTotalAmount()).append("\n\n");
        message.append("We'll notify you when your order ships!\n\n");
        message.append("Best regards,\nE-Commerce Team");
        
        return message.toString();
    }

    @Override
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Root & Branch - Your Account is Ready!");
            message.setText(buildWelcomeMessage(user));
            
            mailSender.send(message);
            log.info("Welcome email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
        }
    }

    private String buildWelcomeMessage(User user) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(user.getFirstName()).append(",\n\n");
        message.append("Welcome to Root & Branch! 🌳\n\n");
        message.append("Thank you for creating an account with us. We're excited to have you as part of our community of handcrafted wood enthusiasts!\n\n");
        message.append("Here's what you can do with your new account:\n");
        message.append("✓ Browse our curated collection of handmade wooden products\n");
        message.append("✓ Save your favorite items to your wishlist\n");
        message.append("✓ Enjoy a seamless checkout experience\n");
        message.append("✓ Track your orders and purchase history\n");
        message.append("✓ Receive exclusive offers and early access to new products\n\n");
        message.append("Your Account Details:\n");
        message.append("Email: ").append(user.getEmail()).append("\n");
        message.append("Name: ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n\n");
        message.append("Ready to start shopping? Visit our website and discover beautiful, sustainable wooden crafts that bring warmth to your home and office.\n\n");
        message.append("If you have any questions, feel free to contact our support team. We're here to help!\n\n");
        message.append("Happy shopping!\n\n");
        message.append("Best regards,\n");
        message.append("The Root & Branch Team\n");
        message.append("Crafting quality, one piece at a time 🪵");
        
        return message.toString();
    }

    private String buildOrderStatusUpdateMessage(Order order, String status) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(order.getUser().getFirstName()).append(",\n\n");
        message.append("Your order #").append(order.getId()).append(" status has been updated to: ");
        message.append(status.toUpperCase()).append("\n\n");
        
        switch (status.toLowerCase()) {
            case "processing":
                message.append("Your order is now being processed and will be shipped soon.");
                break;
            case "shipped":
                message.append("Great news! Your order has been shipped and is on its way to you.");
                break;
            case "delivered":
                message.append("Your order has been delivered. We hope you enjoy your purchase!");
                break;
            case "cancelled":
                message.append("Your order has been cancelled. If you have any questions, please contact us.");
                break;
            default:
                message.append("Your order status has been updated.");
        }
        
        message.append("\n\nBest regards,\nE-Commerce Team");
        return message.toString();
    }
}