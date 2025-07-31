package com.example.obs_test.service;

import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.entity.Item;
import com.example.obs_test.entity.Order;
import com.example.obs_test.repository.ItemRepository;
import com.example.obs_test.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ItemRepository itemRepository;

    public Order placeOrder(Order order) {
        Long itemId = order.getItem().getId();
        int currentStock = inventoryService.calculateStock(itemId);
        if (order.getQty() > currentStock) {
            throw new RuntimeException("Insufficient stock");
        }

        // Withdraw the stock
        AddInventoryRequest inventoryRequest = AddInventoryRequest.builder()
                .itemId(itemId)
                .qty(order.getQty())
                .type("W")
                .build();
        inventoryService.save(inventoryRequest);

        // Save the order
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        order.setPrice(item.getPrice());
        return orderRepository.save(order);
    }

    public Order findById(Long id) {
        return orderRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order update(Long id, Order request) {
        Order order = orderRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        order.setOrderNo(request.getOrderNo());
        order.setQty(request.getQty());
        order.setPrice(request.getPrice());
        order.setItem(request.getItem());
        return orderRepository.save(order);
    }

    public void delete(String orderNo) {
        orderRepository.deleteById(orderNo);
    }
}

