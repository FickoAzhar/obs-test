package com.example.obs_test.service;

import com.example.obs_test.dto.*;
import com.example.obs_test.entity.*;
import com.example.obs_test.repository.*;
import jakarta.persistence.EntityManager;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private InventoryService inventoryService;
    @Mock private ItemService itemService;
    @Mock private ItemRepository itemRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private OrderService orderService;

    private Order sampleOrder;
    private Item sampleItem;
    private OrderRequest sampleRequest;
    private OrderDto sampleOrderDto;

    @BeforeEach
    void setUp() {
        sampleItem = Item.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(1000))
                .remainingStock(10)
                .build();

        sampleOrder = Order.builder()
                .id(1L)
                .orderNo("ORD-00001")
                .item(sampleItem)
                .qty(2)
                .price(BigDecimal.valueOf(2000))
                .build();

        sampleRequest = new OrderRequest(1L, 2);
        sampleOrderDto = OrderDto.builder()
                .orderNo("ORD-00001")
                .qty(2)
                .price(BigDecimal.valueOf(2000))
                .build();
    }

    @Test
    @DisplayName("generateOrderNumber - Should generate order number")
    void generateOrderNumber_ShouldGenerateSequence() {
        // Arrange
        given(entityManager.createNativeQuery(anyString())).willReturn(mock(jakarta.persistence.Query.class));
        given(entityManager.createNativeQuery(anyString()).getSingleResult()).willReturn(1L);

        // Act
        String result = orderService.generateOrderNumber();

        // Assert
        assertEquals("ORD-00001", result);
    }

    @Test
    @DisplayName("placeOrder - Should create new order")
    @Transactional
    void placeOrder_ShouldCreateOrder() {
        // Arrange
        AddInventoryRequest expectedInventoryRequest = AddInventoryRequest.builder()
                .itemId(1L)
                .qty(2)
                .type("W")
                .build();

        InventoryDto inventoryDto = InventoryDto.builder()
                .item(new ItemDto(1L, "Laptop", BigDecimal.valueOf(1000), 8)) // 10 - 2
                .build();

        // Mock untuk sequence generation
        jakarta.persistence.Query mockedQuery = mock(jakarta.persistence.Query.class);
        given(mockedQuery.getSingleResult()).willReturn(1L);
        given(entityManager.createNativeQuery("SELECT NEXT VALUE FOR order_number_seq")).willReturn(mockedQuery);

        // Mock lainnya
        given(inventoryService.save(any(AddInventoryRequest.class))).willReturn(inventoryDto);
        given(itemRepository.findById(1L)).willReturn(Optional.of(sampleItem));
        given(orderRepository.save(any(Order.class))).willReturn(sampleOrder);
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        OrderDto result = orderService.placeOrder(sampleRequest);

        // Assert
        assertNotNull(result);
        assertEquals("ORD-00001", result.getOrderNo());
        verify(inventoryService).save(expectedInventoryRequest);
        verify(orderRepository).save(any(Order.class));

        // Verifikasi sequence generation dipanggil
        verify(entityManager).createNativeQuery("SELECT NEXT VALUE FOR order_number_seq");
        verify(mockedQuery).getSingleResult();
    }

    @Test
    @DisplayName("findByOrderNo - Should return order")
    void findByOrderNo_ShouldReturnOrder() {
        // Arrange
        given(orderRepository.findByOrderNo("ORD-00001")).willReturn(Optional.of(sampleOrder));
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        OrderDto result = orderService.findByOrderNo("ORD-00001");

        // Assert
        assertNotNull(result);
        assertEquals("ORD-00001", result.getOrderNo());
    }

    @Test
    @DisplayName("findByOrderNo - Should throw when not found")
    void findByOrderNo_WhenNotFound_ShouldThrow() {
        given(orderRepository.findByOrderNo(anyString())).willReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.findByOrderNo("INVALID"));
    }

    @Test
    @DisplayName("getAllOrders - Should return page of orders")
    void getAllOrders_ShouldReturnPage() {
        // Arrange
        Page<Order> orderPage = new PageImpl<>(List.of(sampleOrder));
        given(orderRepository.findAll(any(Pageable.class))).willReturn(orderPage);
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        Page<OrderDto> result = orderService.getAllOrders(Pageable.unpaged());

        // Assert
        assertEquals(1, result.getContent().size());
        verify(orderRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("update - Should update order with same item")
    @Transactional
    void update_WithSameItem_ShouldUpdateOrder() {
        // Arrange
        OrderRequest updateRequest = new OrderRequest(1L, 3); // Change qty from 2 to 3
        given(orderRepository.findByOrderNo("ORD-00001")).willReturn(Optional.of(sampleOrder));
        given(itemRepository.findById(1L)).willReturn(Optional.of(sampleItem));
        given(orderRepository.save(any(Order.class))).willReturn(sampleOrder);
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        OrderDto result = orderService.update("ORD-00001", updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(9, sampleItem.getRemainingStock()); // 10 - (3-2) = 9
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("update - Should update order with different item")
    @Transactional
    void update_WithDifferentItem_ShouldUpdateOrder() {
        // Arrange
        Item newItem = Item.builder().id(2L).price(BigDecimal.valueOf(500)).remainingStock(20).build();
        OrderRequest updateRequest = new OrderRequest(2L, 4);

        given(orderRepository.findByOrderNo("ORD-00001")).willReturn(Optional.of(sampleOrder));
        given(itemRepository.findById(2L)).willReturn(Optional.of(newItem));
        given(orderRepository.save(any(Order.class))).willReturn(sampleOrder);
        given(itemService.convertItemToDto(any(Item.class))).willReturn(new ItemDto());

        // Act
        OrderDto result = orderService.update("ORD-00001", updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(12, sampleItem.getRemainingStock()); // 10 + 2 (return old)
        assertEquals(16, newItem.getRemainingStock()); // 20 - 4 (deduct new)
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("delete - Should delete order")
    void delete_ShouldDeleteOrder() {
        // Arrange
        given(orderRepository.findByOrderNo("ORD-00001")).willReturn(Optional.of(sampleOrder));
        willDoNothing().given(orderRepository).delete(any(Order.class));

        // Act
        orderService.delete("ORD-00001");

        // Assert
        verify(orderRepository).delete(sampleOrder);
    }

    @Test
    @DisplayName("convertToDto - Should return null when input is null")
    void convertToDto_WhenNull_ShouldReturnNull() {
        assertNull(orderService.convertToDto(null));
    }
}