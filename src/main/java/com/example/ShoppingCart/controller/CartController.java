package com.example.ShoppingCart.controller;

import com.example.ShoppingCart.Cart;
import com.example.ShoppingCart.Item;
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
            String cartId = cartStore.createCart(request.discountPercentage());
            Cart cart = cartStore.getCart(cartId);
            return ResponseEntity.status(HttpStatus.CREATED).body(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCart(@PathVariable String cartId) {
        if (!cartStore.cartExists(cartId)) {
            return ResponseEntity.notFound().build();
        }
        Cart cart = cartStore.getCart(cartId);
        return ResponseEntity.ok(CartResponse.from(cartId, cart));
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItemToCart(@PathVariable String cartId, @RequestBody AddToCartRequest request) {
        if (!cartStore.cartExists(cartId)) {
            return ResponseEntity.notFound().build();
        }
        Item item = Item.getItemByName(request.itemName());
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Item not found: " + request.itemName()));
        }
        try {
            Cart cart = cartStore.getCart(cartId);
            cart.addItem(item, request.quantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @PutMapping("/{cartId}/items/{itemName}")
    public ResponseEntity<?> updateCartItem(@PathVariable String cartId, @PathVariable String itemName,
                                           @RequestBody UpdateCartItemRequest request) {
        if (!cartStore.cartExists(cartId)) {
            return ResponseEntity.notFound().build();
        }
        try {
            Cart cart = cartStore.getCart(cartId);
            cart.updateItemQuantity(itemName, request.quantity());
            return ResponseEntity.ok(CartResponse.from(cartId, cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/{cartId}/items/{itemName}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable String cartId, @PathVariable String itemName) {
        if (!cartStore.cartExists(cartId)) {
            return ResponseEntity.notFound().build();
        }
        try {
            Cart cart = cartStore.getCart(cartId);
            cart.removeItem(itemName);
            return ResponseEntity.ok(CartResponse.from(cartId, cart));
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Item not in cart: " + itemName));
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> clearCart(@PathVariable String cartId) {
        if (!cartStore.cartExists(cartId)) {
            return ResponseEntity.notFound().build();
        }
        Cart cart = cartStore.getCart(cartId);
        cart.clear();
        return ResponseEntity.ok(CartResponse.from(cartId, cart));
    }

    record ErrorMessage(String error) {}
}
