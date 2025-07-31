package com.geraldikem.ecommerceapp.service;

import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, String customerId) throws Exception {
        try {
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
            
            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency.toLowerCase())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    );
            
            if (customerId != null && !customerId.isEmpty()) {
                paramsBuilder.setCustomer(customerId);
            }
            
            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());
            log.info("Created PaymentIntent: {}", paymentIntent.getId());
            return paymentIntent;
        } catch (Exception e) {
            log.error("Error creating PaymentIntent", e);
            throw new Exception("Failed to create payment intent: " + e.getMessage());
        }
    }

    @Override
    public PaymentIntent confirmPaymentIntent(String paymentIntentId) throws Exception {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder().build();
            PaymentIntent confirmedPaymentIntent = paymentIntent.confirm(params);
            
            log.info("Confirmed PaymentIntent: {}", confirmedPaymentIntent.getId());
            return confirmedPaymentIntent;
        } catch (Exception e) {
            log.error("Error confirming PaymentIntent", e);
            throw new Exception("Failed to confirm payment: " + e.getMessage());
        }
    }

    @Override
    public String createCustomer(String email, String name) throws Exception {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setName(name)
                    .setEmail(email)
                    .build();
            
            Customer customer = Customer.create(params);
            log.info("Created Stripe customer: {}", customer.getId());
            return customer.getId();
        } catch (Exception e) {
            log.error("Error creating Stripe customer", e);
            throw new Exception("Failed to create customer: " + e.getMessage());
        }
    }
}