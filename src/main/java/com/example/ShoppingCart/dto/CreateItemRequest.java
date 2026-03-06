package com.example.ShoppingCart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateItemRequest(
        @NotBlank(message = "Name is required") String name,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity,
        @Min(value = 1, message = "Price must be at least 1") int price) {
}
