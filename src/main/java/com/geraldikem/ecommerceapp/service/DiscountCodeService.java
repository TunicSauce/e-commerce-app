package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.model.DiscountCode;
import java.util.List;

public interface DiscountCodeService {
    DiscountCode createDiscountCode(DiscountCode discountCode);
    List<DiscountCode> getAllDiscountCodes();
    void toggleDiscountCodeStatus(Long id);
    void deleteDiscountCode(Long id);
}