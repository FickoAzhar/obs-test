package com.example.obs_test.controller;

import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.entity.Inventory;
import com.example.obs_test.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    public Inventory getById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    @GetMapping
    public Page<Inventory> getAllItems(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return inventoryService.getAll(pageable);
    }

    @PostMapping
    public Inventory addInventory(@RequestBody AddInventoryRequest addInventoryRequest) {
        return inventoryService.save(addInventoryRequest);
    }

    @PutMapping("/{id}")
    public Inventory updateInventory(@PathVariable Long id, @RequestBody Inventory updated) {
        return inventoryService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.delete(id);
    }
}
