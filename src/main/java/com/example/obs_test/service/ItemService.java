package com.example.obs_test.service;

import com.example.obs_test.entity.Item;
import com.example.obs_test.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final InventoryService inventoryService;

    public Page<Item> getAllItemsWithStock(Pageable pageable) {
        Page<Item> itemsPage = itemRepository.findAll(pageable);
        itemsPage.forEach(item -> {
            int stock = inventoryService.calculateStock(item.getId());
            item.setRemainingStock(stock);
        });
        return itemsPage;
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Item update(Long id, Item request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        item.setName(request.getName());
        item.setRemainingStock(request.getRemainingStock());
        item.setPrice(request.getPrice());
        return itemRepository.save(item);
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }
}

