package com.example.ShoppingCart.controller;

import com.example.ShoppingCart.Cart;
import com.example.ShoppingCart.Item;
import com.example.ShoppingCart.Logger;
import com.example.ShoppingCart.dto.AddToCartRequest;
import com.example.ShoppingCart.dto.CartResponse;
import com.example.ShoppingCart.dto.CreateCartRequest;
import com.example.ShoppingCart.dto.UpdateCartItemRequest;
import com.example.ShoppingCart.service.CartStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartStore cartStore;

    public CartController(CartStore cartStore) {
        this.cartStore = cartStore;
    }

    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody CreateCartRequest request) {
        try {
            Logger.getInstance()
                    .info("Received request to create cart with discount: " + request.discountPercentage() + "%");
            String cartId = cartStore.createCart(request.discountPercentage());
            Cart cart = cartStore.getCart(cartId);
            return ResponseEntity.status(HttpStatus.CREATED).body(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().error("Failed to create cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable String cartId) {
        if (!cartStore.cartExists(cartId)) {
            Logger.getInstance().warn("Attempted to retrieve non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        Logger.getInstance().info("Retrieving cart: " + cartId);
        Cart cart = cartStore.getCart(cartId);
        return ResponseEntity.ok(CartResponse.from(cartId, cart));
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItemToCart(@PathVariable String cartId, @RequestBody AddToCartRequest request) {
        if (!cartStore.cartExists(cartId)) {
            Logger.getInstance().warn("Attempted to add items to non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        Item item = Item.getItemByName(request.itemName());
        if (item == null) {
            Logger.getInstance().error("Item not found: " + request.itemName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Item not found: " + request.itemName()));
        }
        try {
            Logger.getInstance().info("Adding item to cart - Cart ID: " + cartId + ", Item: " + request.itemName()
                    + ", Qty: " + request.quantity());
            Cart cart = cartStore.getCart(cartId);
            cart.addItem(item, request.quantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().error("Failed to add item to cart: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @PutMapping("/{cartId}/items/{itemName}")
    public ResponseEntity<?> updateCartItem(@PathVariable String cartId, @PathVariable String itemName,
            @RequestBody UpdateCartItemRequest request) {
        if (!cartStore.cartExists(cartId)) {
            Logger.getInstance().warn("Attempted to update item in non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        try {
            Logger.getInstance().info("Updating cart item - Cart ID: " + cartId + ", Item: " + itemName + ", New Qty: "
                    + request.quantity());
            Cart cart = cartStore.getCart(cartId);
            cart.updateItemQuantity(itemName, request.quantity());
            return ResponseEntity.ok(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            Logger.getInstance().error("Failed to update cart item: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/{cartId}/items/{itemName}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable String cartId, @PathVariable String itemName) {
        if (!cartStore.cartExists(cartId)) {
            Logger.getInstance().warn("Attempted to remove item from non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        try {
            Logger.getInstance().info("Removing item from cart - Cart ID: " + cartId + ", Item: " + itemName);
            Cart cart = cartStore.getCart(cartId);
            cart.removeItem(itemName);
            return ResponseEntity.ok(CartResponse.from(cartId, cart));
        } catch (NullPointerException e) {
            Logger.getInstance().error("Failed to remove item (Not in cart): " + itemName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Item not in cart: " + itemName));
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> clearCart(@PathVariable String cartId) {
        if (!cartStore.cartExists(cartId)) {
            Logger.getInstance().warn("Attempted to clear non-existent cart: " + cartId);
            return ResponseEntity.notFound().build();
        }
        Logger.getInstance().info("Clearing cart - Cart ID: " + cartId);
        Cart cart = cartStore.getCart(cartId);
        cart.clear();
        return ResponseEntity.ok(CartResponse.from(cartId, cart));
    }

    record ErrorMessage(String error) {
    }
}
