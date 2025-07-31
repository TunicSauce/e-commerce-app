package com.geraldikem.ecommerceapp.service;

import java.math.BigDecimal;

public interface TaxService {
    BigDecimal calculateTax(BigDecimal subtotal);
    BigDecimal getTaxRate();
}