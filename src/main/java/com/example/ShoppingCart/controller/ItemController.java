package com.example.ShoppingCart.controller;

import com.example.ShoppingCart.dto.CreateItemRequest;
import com.example.ShoppingCart.dto.ItemResponse;
import com.example.ShoppingCart.service.Item;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private static final Logger logger = Logger.getLogger(ItemController.class.getName());

    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody CreateItemRequest request) {
        try {
            logger.info("Received request to create item: " + request.name());
            Item item = new Item(request.name(), request.quantity(), request.price());
            return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.from(item));
        } catch (IllegalArgumentException e) {
            logger.severe("Failed to create item (Conflict): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            logger.severe("Failed to create item (Bad Request): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping
    public List<ItemResponse> listItems() {
        logger.info("Listing all items.");
        return Item.getAllItems().stream()
                .map(ItemResponse::from)
                .toList();
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getItem(@PathVariable String name) {
        Item item = Item.getItemByName(name);
        if (item == null) {
            logger.warning("Attempted to retrieve non-existent item: " + name);
            return ResponseEntity.notFound().build();
        }
        logger.info("Retrieving item info for: " + name);
        return ResponseEntity.ok(ItemResponse.from(item));
    }

    record ErrorMessage(String error) {
    }
}
