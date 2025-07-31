package com.geraldikem.ecommerceapp.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxServiceImpl implements TaxService {

    // HST Rate for Ontario
    private static final BigDecimal HST_RATE = new BigDecimal("0.13");

    public BigDecimal calculateTax(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(HST_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getTaxRate() {
        return HST_RATE;
    }
}