package com.geraldikem.ecommerceapp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ShoppingCartDto {
    private List<CartItemDto> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private String appliedDiscountCode;
    private BigDecimal discountAmount;
    private BigDecimal grandTotal;
}