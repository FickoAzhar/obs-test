package com.example.obs_test.controller;

import com.example.obs_test.dto.OrderDto;
import com.example.obs_test.dto.OrderRequest;
import com.example.obs_test.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

	private final OrderService orderService;

	@GetMapping("/{orderNo}")
	public ResponseEntity<OrderDto> getByOrderNo(@PathVariable String orderNo) {
		return ResponseEntity.ok(orderService.findByOrderNo(orderNo));
	}

	@GetMapping
	public ResponseEntity<Page<OrderDto>> getAllOrders(
			@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		Page<OrderDto> result = orderService.getAllOrders(pageable);
		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<OrderDto> placeOrder(@Valid @RequestBody OrderRequest request) {
		return ResponseEntity.ok(orderService.placeOrder(request));
	}

	@PutMapping("/{orderNo}")
	public ResponseEntity<OrderDto> updateInventory(@PathVariable String orderNo, @RequestBody OrderRequest request) {
		return ResponseEntity.ok(orderService.update(orderNo, request));
	}

	@DeleteMapping("/{orderNo}")
	public ResponseEntity<String> deleteOrder(@PathVariable String orderNo) {
		orderService.delete(orderNo);
		return ResponseEntity.ok("order No " + orderNo + " deleted successfully");
	}
}
