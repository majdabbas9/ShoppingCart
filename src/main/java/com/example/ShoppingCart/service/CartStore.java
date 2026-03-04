package com.example.ShoppingCart.service;

import com.example.ShoppingCart.Cart;
import com.example.ShoppingCart.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CartStore {
    private final Map<String, Cart> carts = new ConcurrentHashMap<>();

    public String createCart(double discountPercentage) {
        String cartId = UUID.randomUUID().toString();
        Logger.getInstance().info("Creating new cart with ID: " + cartId + " and discount " + discountPercentage + "%");
        carts.put(cartId, new Cart(discountPercentage));
        return cartId;
    }

    public Cart getCart(String cartId) {
        return carts.get(cartId);
    }

    public boolean cartExists(String cartId) {
        return carts.containsKey(cartId);
    }
}
