package com.example.ShoppingCart.service;

import com.example.ShoppingCart.Cart;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CartStore {
    private final Map<String, Cart> carts = new ConcurrentHashMap<>();

    public String createCart(double discountPercentage) {
        String cartId = UUID.randomUUID().toString();
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
