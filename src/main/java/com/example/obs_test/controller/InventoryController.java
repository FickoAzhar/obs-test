package com.example.obs_test.controller;

import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.dto.InventoryDto;
import com.example.obs_test.service.InventoryService;
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
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<InventoryDto>> getAllItems(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<InventoryDto> addInventory(@Valid @RequestBody AddInventoryRequest addInventoryRequest) {
        return ResponseEntity.ok(inventoryService.save(addInventoryRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDto> updateInventory(@PathVariable Long id, @RequestBody AddInventoryRequest updated) {
        return ResponseEntity.ok(inventoryService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventory(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.ok("Inventory with ID " + id + " deleted successfully");
    }
}
