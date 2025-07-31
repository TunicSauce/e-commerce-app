package com.geraldikem.ecommerceapp.util;

import com.geraldikem.ecommerceapp.model.Product;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeliveryEstimator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    public static String getEstimatedDeliveryDate(Product product) {
        int deliveryDays = product.getEstimatedDeliveryDays() != null ? product.getEstimatedDeliveryDays() : 7;
        LocalDate estimatedDate = LocalDate.now().plusDays(deliveryDays);
        return estimatedDate.format(DATE_FORMATTER);
    }

    public static String getEstimatedDeliveryDate(int deliveryDays) {
        LocalDate estimatedDate = LocalDate.now().plusDays(deliveryDays);
        return estimatedDate.format(DATE_FORMATTER);
    }

    public static String getEstimatedDeliveryDateRange(List<Product> products) {
        if (products.isEmpty()) {
            return "No products in cart";
        }

        int minDays = products.stream()
                .mapToInt(p -> p.getEstimatedDeliveryDays() != null ? p.getEstimatedDeliveryDays() : 7)
                .min()
                .orElse(7);

        int maxDays = products.stream()
                .mapToInt(p -> p.getEstimatedDeliveryDays() != null ? p.getEstimatedDeliveryDays() : 7)
                .max()
                .orElse(7);

        if (minDays == maxDays) {
            return getEstimatedDeliveryDate(minDays);
        } else {
            LocalDate minDate = LocalDate.now().plusDays(minDays);
            LocalDate maxDate = LocalDate.now().plusDays(maxDays);
            return minDate.format(DATE_FORMATTER) + " - " + maxDate.format(DATE_FORMATTER);
        }
    }

    public static String getEstimatedDeliveryRange(Product product) {
        int deliveryDays = product.getEstimatedDeliveryDays() != null ? product.getEstimatedDeliveryDays() : 7;
        LocalDate startDate = LocalDate.now().plusDays(deliveryDays);
        LocalDate endDate = startDate.plusDays(2); // Add 2 days buffer for range
        return startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER);
    }
}