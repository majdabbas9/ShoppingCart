package com.example.ShoppingCart.controller;

import com.example.ShoppingCart.Item;
import com.example.ShoppingCart.dto.CreateItemRequest;
import com.example.ShoppingCart.dto.ItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody CreateItemRequest request) {
        try {
            Item item = new Item(request.name(), request.quantity(), request.price());
            return ResponseEntity.status(HttpStatus.CREATED).body(ItemResponse.from(item));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping
    public List<ItemResponse> listItems() {
        return Item.getAllItems().stream()
                .map(ItemResponse::from)
                .toList();
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getItem(@PathVariable String name) {
        Item item = Item.getItemByName(name);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ItemResponse.from(item));
    }

    record ErrorMessage(String error) {}
}
