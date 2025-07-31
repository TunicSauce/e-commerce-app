package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.*;
import com.geraldikem.ecommerceapp.repository.OrderRepository;
import com.geraldikem.ecommerceapp.repository.ProductRepository;
import com.geraldikem.ecommerceapp.repository.ShoppingCartRepository;
import com.geraldikem.ecommerceapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final TaxService taxService;
    private final EmailService emailService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            ShoppingCartRepository shoppingCartRepository,
                            ProductRepository productRepository,
                            TaxService taxService,
                            EmailService emailService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.productRepository = productRepository;
        this.taxService = taxService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Order createOrder(String userEmail, String shippingAddress, String shippingCity, String shippingProvince, String shippingPostalCode) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Shopping cart not found for user: " + userEmail));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create an order from an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setShippingCity(shippingCity);
        order.setShippingProvince(shippingProvince);
        order.setShippingPostalCode(shippingPostalCode);
        order.setStatus("PENDING");

        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantityToOrder = cartItem.getQuantity();

            if (product.getStockQuantity() < quantityToOrder) {
                throw new IllegalStateException("Not enough stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - quantityToOrder);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantityToOrder);
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        BigDecimal subtotal = orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = taxService.calculateTax(subtotal);
        BigDecimal grandTotal = subtotal.add(tax);
        order.setTotalAmount(grandTotal);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        shoppingCartRepository.save(cart);

        return savedOrder;
    }

    @Override
    public List<Order> findOrdersByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return orderRepository.findTotalRevenue();
    }

    @Override
    public long getTotalOrdersCount() {
        return orderRepository.count();
    }

    @Override
    public List<Order> getRecentOrders() {
        return orderRepository.findTop5ByOrderByOrderDateDesc();
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + orderId));
        String oldStatus = order.getStatus();
        order.setStatus(status);
        orderRepository.save(order);
        
        // Send email notification if status changed
        if (!oldStatus.equals(status)) {
            emailService.sendOrderStatusUpdateEmail(order, status);
        }
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }
}