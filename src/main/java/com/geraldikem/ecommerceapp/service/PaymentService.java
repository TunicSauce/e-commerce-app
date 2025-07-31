package com.geraldikem.ecommerceapp.service;

import com.stripe.model.PaymentIntent;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentIntent createPaymentIntent(BigDecimal amount, String currency, String customerId) throws Exception;
    PaymentIntent confirmPaymentIntent(String paymentIntentId) throws Exception;
    String createCustomer(String email, String name) throws Exception;
}