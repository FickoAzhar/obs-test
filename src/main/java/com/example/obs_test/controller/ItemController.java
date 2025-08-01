package com.example.obs_test.controller;

import com.example.obs_test.dto.ItemDto;
import com.example.obs_test.dto.ItemRequest;
import com.example.obs_test.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ItemDto>> getAllItems(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(itemService.getAllItemsWithStock(pageable));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemRequest item) {
        return ResponseEntity.ok(itemService.save(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @RequestBody ItemRequest request) {
        return ResponseEntity.ok(itemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.ok("Item with ID " + id + " deleted successfully");
    }
}

