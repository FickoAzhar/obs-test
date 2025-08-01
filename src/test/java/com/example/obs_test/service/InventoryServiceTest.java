package com.example.obs_test.service;

import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.dto.InventoryDto;
import com.example.obs_test.dto.ItemDto;
import com.example.obs_test.entity.Inventory;
import com.example.obs_test.entity.Item;
import com.example.obs_test.exception.DataNotFoundException;
import com.example.obs_test.repository.InventoryRepository;
import com.example.obs_test.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private InventoryService inventoryService;

    private Item sampleItem;
    private Inventory sampleInventory;
    private AddInventoryRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleItem = Item.builder()
                .id(1L)
                .name("Laptop")
                .remainingStock(10)
                .build();

        sampleInventory = Inventory.builder()
                .id(1L)
                .item(sampleItem)
                .qty(5)
                .type("IN")
                .build();

        sampleRequest = new AddInventoryRequest(1L, 5, "T");
    }

    @Test
    @DisplayName("save - Should save inventory with type T")
    void save_WithTypeIN_ShouldIncreaseStock() {
        // Arrange
        given(itemRepository.findById(1L)).willReturn(Optional.of(sampleItem));
        given(inventoryRepository.save(any(Inventory.class))).willReturn(sampleInventory);
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        InventoryDto result = inventoryService.save(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals(15, sampleItem.getRemainingStock()); // 10 + 5
        verify(itemRepository, times(1)).save(sampleItem);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("save - Should save inventory with type W")
    void save_WithTypeW_ShouldDecreaseStock() {
        // Arrange
        AddInventoryRequest withdrawalRequest = new AddInventoryRequest(1L, 3, "W");
        given(itemRepository.findById(1L)).willReturn(Optional.of(sampleItem));
        given(inventoryRepository.save(any(Inventory.class))).willReturn(sampleInventory);
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        InventoryDto result = inventoryService.save(withdrawalRequest);

        // Assert
        assertNotNull(result);
        assertEquals(7, sampleItem.getRemainingStock()); // 10 - 3
        verify(itemRepository, times(1)).save(sampleItem);
    }

    @Test
    @DisplayName("save - Should throw exception when insufficient stock")
    void save_WithInsufficientStock_ShouldThrowException() {
        // Arrange
        AddInventoryRequest invalidRequest = new AddInventoryRequest(1L, 15, "W");
        given(itemRepository.findById(1L)).willReturn(Optional.of(sampleItem));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> inventoryService.save(invalidRequest));
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("findById - Should return inventory when exists")
    void findById_WhenExists_ShouldReturnInventory() {
        // Arrange
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(sampleInventory));
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        InventoryDto result = inventoryService.findById(1L);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - Should throw exception when not found")
    void findById_WhenNotExists_ShouldThrowException() {
        // Arrange
        given(inventoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFoundException.class, () -> inventoryService.findById(1L));
    }

    @Test
    @DisplayName("getAll - Should return page of inventories")
    void getAll_ShouldReturnPage() {
        // Arrange
        Page<Inventory> page = new PageImpl<>(List.of(sampleInventory));
        given(inventoryRepository.findAll(any(Pageable.class))).willReturn(page);
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        Page<InventoryDto> result = inventoryService.getAll(Pageable.unpaged());

        // Assert
        assertEquals(1, result.getContent().size());
        verify(inventoryRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("delete - Should delete inventory")
    void delete_ShouldDeleteInventory() {
        // Arrange
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(sampleInventory));
        willDoNothing().given(inventoryRepository).delete(any(Inventory.class));

        // Act
        inventoryService.delete(1L);

        // Assert
        verify(inventoryRepository, times(1)).delete(sampleInventory);
    }

    @Test
    @DisplayName("update - Should update inventory with different item")
    void update_WithDifferentItem_ShouldUpdateCorrectly() {
        // Arrange
        Item newItem = Item.builder().id(2L).remainingStock(20).build();
        AddInventoryRequest updateRequest = new AddInventoryRequest(2L, 5, "T");

        given(inventoryRepository.findById(1L)).willReturn(Optional.of(sampleInventory));
        given(itemRepository.findById(2L)).willReturn(Optional.of(newItem));
        given(inventoryRepository.save(any(Inventory.class))).willReturn(sampleInventory);
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        InventoryDto result = inventoryService.update(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(5, sampleItem.getRemainingStock()); // 10 - 5 (return old stock)
        assertEquals(25, newItem.getRemainingStock()); // 20 + 5 (add new stock)
        verify(itemRepository, times(2)).save(any(Item.class));
    }

    @Test
    @DisplayName("convertToDto - Should return null when input is null")
    void convertToDto_WhenNull_ShouldReturnNull() {
        assertNull(inventoryService.convertToDto(null));
    }
}
