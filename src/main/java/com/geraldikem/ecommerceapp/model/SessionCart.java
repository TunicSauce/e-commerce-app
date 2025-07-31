package com.geraldikem.ecommerceapp.model;

import com.geraldikem.ecommerceapp.dto.CartItemDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SessionCart {

    private List<CartItemDto> items = new ArrayList<>();
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public void addItem(CartItemDto newItem) {
        for (CartItemDto item : items) {
            if (item.getProductId().equals(newItem.getProductId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                recalculateTotalPrice();
                return;
            }
        }
        items.add(newItem);
        recalculateTotalPrice();
    }


    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        recalculateTotalPrice();
    }


    public void recalculateTotalPrice() {
        this.totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}