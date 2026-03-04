package com.example.ShoppingCart.dto;

import com.example.ShoppingCart.Item;

public record ItemResponse(int id, String name, int quantity, int price) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getQuantity(), item.getPrice());
    }
}
