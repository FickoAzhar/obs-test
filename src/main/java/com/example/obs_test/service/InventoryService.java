package com.example.obs_test.service;

import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.entity.Inventory;
import com.example.obs_test.entity.Item;
import com.example.obs_test.repository.InventoryRepository;
import com.example.obs_test.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    public Inventory save(AddInventoryRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        int qty = request.getQty();
        if (request.getType().equalsIgnoreCase("W")) {
            int stock = calculateStock(request.getItemId());
            if (qty > stock) {
                throw new RuntimeException("Insufficient stock");
            }
            qty = -qty;
        }

        Inventory inventory = Inventory.builder()
                .item(item)
                .qty(qty)
                .type(request.getType())
                .build();

        return inventoryRepository.save(inventory);
    }

    public Inventory findById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Page<Inventory> getAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    public int calculateStock(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return inventoryRepository.findByItem(item).stream()
                .mapToInt(Inventory::getQty)
                .sum();
    }

    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }

    public Inventory update(Long id, Inventory updated) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.setQty(updated.getQty());
        inventory.setType(updated.getType());
        return inventoryRepository.save(inventory);
    }
}

