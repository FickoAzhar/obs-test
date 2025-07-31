package com.example.obs_test;

import com.example.obs_test.controller.OrderController;
import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.entity.Item;
import com.example.obs_test.entity.Order;
import com.example.obs_test.repository.ItemRepository;
import com.example.obs_test.repository.OrderRepository;
import com.example.obs_test.service.InventoryService;
import com.example.obs_test.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ItemRepository itemRepo;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPlaceOrder_Success() {
        Item item = Item.builder().id(1L).price(10000).build();
        Order order = Order.builder().orderNo("O01").item(item).qty(2).build();
        AddInventoryRequest inventoryRequest = AddInventoryRequest.builder().type("W").qty(2).itemId(1L).build();

        when(inventoryService.calculateStock(1L)).thenReturn(10);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placeOrder(order);

        assertEquals("O01", result.getOrderNo());
        assertEquals(10000, result.getPrice());
        verify(inventoryService).save(inventoryRequest);
    }

    @Test
    void testPlaceOrder_InsufficientStock_ShouldThrow() {
        Item item = Item.builder().id(1L).build();
        Order order = Order.builder().orderNo("O02").item(item).qty(5).build();

        when(inventoryService.calculateStock(1L)).thenReturn(2);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                orderService.placeOrder(order));

        assertEquals("Insufficient stock", ex.getMessage());
        verify(orderRepo, never()).save(any());
    }
}

