package com.example.ShoppingCart.controller;

import com.example.ShoppingCart.dto.AddToCartRequest;
import com.example.ShoppingCart.dto.CartResponse;
import com.example.ShoppingCart.dto.CreateCartRequest;
import com.example.ShoppingCart.dto.UpdateCartItemRequest;
import com.example.ShoppingCart.service.Cart;
import com.example.ShoppingCart.service.CartStore;
import com.example.ShoppingCart.service.Item;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private static final Logger logger = Logger.getLogger(CartController.class.getName());

    private final CartStore cartStore;

    public CartController(CartStore cartStore) {
        this.cartStore = cartStore;
    }

    @PostMapping
    public ResponseEntity<?> createCart(@Valid @RequestBody CreateCartRequest request) {
        try {
            logger.info("Received request to create cart with discount: " + request.discountPercentage() + "%");
            String cartId = cartStore.createCart(request.discountPercentage());
            Cart cart = cartStore.getCart(cartId);
            return ResponseEntity.status(HttpStatus.CREATED).body(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            logger.severe("Failed to create cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable String cartId) {
        if (!cartStore.cartExists(cartId)) {
            logger.warning("Attempted to retrieve non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Retrieving cart: " + cartId);
        Cart cart = cartStore.getCart(cartId);
        return ResponseEntity.ok(CartResponse.from(cartId, cart));
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItemToCart(@PathVariable String cartId, @Valid @RequestBody AddToCartRequest request) {
        if (!cartStore.cartExists(cartId)) {
            logger.warning("Attempted to add items to non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        Item item = Item.getItemByName(request.itemName());
        if (item == null) {
            logger.severe("Item not found: " + request.itemName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Item not found: " + request.itemName()));
        }
        try {
            logger.info("Adding item to cart - Cart ID: " + cartId + ", Item: " + request.itemName()
                    + ", Qty: " + request.quantity());
            Cart cart = cartStore.getCart(cartId);
            cart.addItem(item, request.quantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            logger.severe("Failed to add item to cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @PutMapping("/{cartId}/items/{itemName}")
    public ResponseEntity<?> updateCartItem(@PathVariable String cartId, @PathVariable String itemName,
            @Valid @RequestBody UpdateCartItemRequest request) {
        if (!cartStore.cartExists(cartId)) {
            logger.warning("Attempted to update item in non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        try {
            logger.info("Updating cart item - Cart ID: " + cartId + ", Item: " + itemName + ", New Qty: "
                    + request.quantity());
            Cart cart = cartStore.getCart(cartId);
            cart.updateItemQuantity(itemName, request.quantity());
            return ResponseEntity.ok(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            logger.severe("Failed to update cart item: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/{cartId}/items/{itemName}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable String cartId, @PathVariable String itemName) {
        if (!cartStore.cartExists(cartId)) {
            logger.warning("Attempted to remove item from non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        try {
            logger.info("Removing item from cart - Cart ID: " + cartId + ", Item: " + itemName);
            Cart cart = cartStore.getCart(cartId);
            cart.removeItem(itemName);
            return ResponseEntity.ok(CartResponse.from(cartId, cart));
        } catch (NullPointerException e) {
            logger.severe("Failed to remove item (Not in cart): " + itemName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Item not in cart: " + itemName));
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> clearCart(@PathVariable String cartId) {
        if (!cartStore.cartExists(cartId)) {
            logger.warning("Attempted to clear non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Clearing cart - Cart ID: " + cartId);
        Cart cart = cartStore.getCart(cartId);
        cart.clear();
        return ResponseEntity.ok(CartResponse.from(cartId, cart));
    }

    record ErrorMessage(String error) {
    }
}
