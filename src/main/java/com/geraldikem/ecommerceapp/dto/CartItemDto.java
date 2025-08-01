package com.geraldikem.ecommerceapp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}