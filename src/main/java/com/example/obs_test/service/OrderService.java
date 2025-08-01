package com.example.obs_test.service;

import com.example.obs_test.dto.*;
import com.example.obs_test.entity.Inventory;
import com.example.obs_test.entity.Item;
import com.example.obs_test.entity.Order;
import com.example.obs_test.repository.InventoryRepository;
import com.example.obs_test.repository.ItemRepository;
import com.example.obs_test.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final InventoryService inventoryService;
	private final ItemService itemService;
	private final ItemRepository itemRepository;
	private final InventoryRepository inventoryRepository;

	private final EntityManager entityManager;

	public String generateOrderNumber() {
		try {
			Long seq = (Long) entityManager.createNativeQuery("SELECT NEXT VALUE FOR order_number_seq")
					.getSingleResult();
			log.info("get sequence : {}", seq);
			return String.format("ORD-%05d", seq.longValue());
		} catch (Exception e) {
			log.error("Failed to generate order number", e);
			throw new RuntimeException("Order number generation failed", e);
		}
	}

	@Transactional
	public OrderDto placeOrder(OrderRequest request) {
		log.info("Processing inventory for order...");
		AddInventoryRequest inventoryRequest = AddInventoryRequest.builder()
				.itemId(request.getItemId())
				.qty(request.getQty())
				.type("W")
				.build();

		InventoryDto inventoryDto = inventoryService.save(inventoryRequest);

		log.info("Creating order...");
		Item item = itemRepository.findById(inventoryDto.getItem().getId())
				.orElseThrow(() -> new RuntimeException("Item not found after inventory update"));

		BigDecimal totalPrice = item.getPrice().multiply(BigDecimal.valueOf(request.getQty()));

		Order order = Order.builder()
				.orderNo(generateOrderNumber())
				.item(item)
				.qty(request.getQty())
				.price(totalPrice)
				.build();

		order = orderRepository.save(order);
		log.info("Order saved successfully: {}", order);

		return convertToDto(order);
	}

	public OrderDto findByOrderNo(String orderNo) {
		log.info("get Request : {}", orderNo);
		Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(() -> new RuntimeException("Order not found"));
		return convertToDto(order);
	}

	@Transactional(readOnly = true)
	public Page<OrderDto> getAllOrders(Pageable pageable) {
		return orderRepository.findAll(pageable).map(order -> {
			Hibernate.initialize(order.getItem());
			return convertToDto(order);
		});
	}

	@Transactional
	public OrderDto update(String orderNo, OrderRequest request) {
		Order order = orderRepository.findByOrderNo(orderNo)
				.orElseThrow(() -> new RuntimeException("Order not found"));

		Item newItem = itemRepository.findById(request.getItemId())
				.orElseThrow(() -> new EntityNotFoundException("Item not found"));

		Item oldItem = order.getItem();
		if (!oldItem.getId().equals(request.getItemId())) {
			oldItem.setRemainingStock(oldItem.getRemainingStock() + order.getQty());
			itemRepository.save(oldItem);
		}

		int stockChange = order.getItem().getId().equals(request.getItemId())
				? order.getQty() - request.getQty()
				: -request.getQty();

		int newStock = newItem.getRemainingStock() + stockChange;
		if (newStock < 0) throw new RuntimeException("Insufficient stock");

		newItem.setRemainingStock(newStock);
		itemRepository.save(newItem);

		inventoryRepository.save(Inventory.builder()
				.item(newItem)
				.qty(request.getQty())
				.type("W")
				.build());

		order.setQty(request.getQty());
		order.setItem(newItem);
		order.setPrice(newItem.getPrice().multiply(BigDecimal.valueOf(request.getQty())));

		return convertToDto(orderRepository.save(order));
	}

	public void delete(String orderNo) {
		Order order = orderRepository.findByOrderNo(orderNo)
				.orElseThrow(() -> new RuntimeException("Order not found"));
		orderRepository.delete(order);
	}

	public OrderDto convertToDto(Order order) {
		if (order == null)
			return null;

		return OrderDto.builder()
				.orderNo(order.getOrderNo())
				.item(itemService.convertItemToDto(order.getItem()))
				.qty(order.getQty())
				.price(order.getPrice())
				.build();
	}
}

