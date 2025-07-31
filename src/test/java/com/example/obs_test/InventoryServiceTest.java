package com.example.obs_test;

import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.entity.Inventory;
import com.example.obs_test.entity.Item;
import com.example.obs_test.repository.InventoryRepository;
import com.example.obs_test.repository.ItemRepository;
import com.example.obs_test.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepo;

    @Mock
    private ItemRepository itemRepo;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateStock_Success() {
        Item item = Item.builder().id(1L).build();
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepo.findByItem(item)).thenReturn(List.of(
                Inventory.builder().qty(10).build(),
                Inventory.builder().qty(-4).build()
        ));

        int stock = inventoryService.calculateStock(1L);
        assertEquals(6, stock);
    }

    @Test
    void testSave_WithdrawWithSufficientStock() {
        Item item = Item.builder().id(1L).build();
        AddInventoryRequest inventoryRequest = AddInventoryRequest.builder().type("W").qty(5).itemId(1L).build();
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepo.findByItem(item)).thenReturn(List.of(
                Inventory.builder().qty(10).build()
        ));
        when(inventoryRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Inventory result = inventoryService.save(inventoryRequest);
        assertEquals(-5, result.getQty());
    }

    @Test
    void testSave_WithdrawWithInsufficientStock_ShouldThrow() {
        Item item = Item.builder().id(1L).build();
        AddInventoryRequest inventoryRequest = AddInventoryRequest.builder().type("W").qty(5).itemId(1L).build();
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepo.findByItem(item)).thenReturn(List.of(
                Inventory.builder().qty(3).build()
        ));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                inventoryService.save(inventoryRequest));

        assertEquals("Insufficient stock", ex.getMessage());
    }
}
