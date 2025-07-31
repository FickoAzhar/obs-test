package com.example.obs_test.controller;

import com.example.obs_test.entity.Order;
import com.example.obs_test.service.OrderService;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public Order getById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @GetMapping
    public Page<Order> getAllOrders(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        return orderService.placeOrder(order);
    }

    @PutMapping("/{id}")
    public Order updateInventory(@PathVariable Long id, @RequestBody Order request) {
        return orderService.update(id, request);
    }

    @DeleteMapping("/{orderNo}")
    public void deleteOrder(@PathVariable String orderNo) {
        orderService.delete(orderNo);
    }
}
