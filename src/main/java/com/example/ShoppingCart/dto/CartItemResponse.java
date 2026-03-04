package com.example.ShoppingCart.dto;

import com.example.ShoppingCart.CartItem;

public record CartItemResponse(String itemName, int quantity, int totalPrice) {
    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getStockItem().getName(),
                cartItem.getQuantity(),
                cartItem.getTotalPrice()
        );
    }
}
