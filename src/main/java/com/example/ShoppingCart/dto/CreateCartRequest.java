package com.example.ShoppingCart.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record CreateCartRequest(
        @DecimalMin(value = "0.0", message = "Discount cannot be negative") @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%") double discountPercentage) {
}
