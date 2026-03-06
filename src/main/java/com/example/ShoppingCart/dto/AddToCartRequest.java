package com.example.ShoppingCart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddToCartRequest(
        @NotBlank(message = "Item name is required") String itemName,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity) {
}
