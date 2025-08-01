package com.example.obs_test.service;

import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.dto.InventoryDto;
import com.example.obs_test.entity.Inventory;
import com.example.obs_test.entity.Item;
import com.example.obs_test.exception.DataNotFoundException;
import com.example.obs_test.repository.InventoryRepository;
import com.example.obs_test.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

	private final InventoryRepository inventoryRepository;
	private final ItemRepository itemRepository;
	private final ItemService itemService;

	@Transactional
	public InventoryDto save(AddInventoryRequest request) {
		Item item = itemRepository.findById(request.getItemId())
				.orElseThrow(() -> new DataNotFoundException(request.getItemId()));
		log.info("get item : {}", item);

		int qty = request.getQty();
		int stock = item.getRemainingStock();
		log.info("get Stock : {}", stock);
		if (request.getType().equalsIgnoreCase("W")) {
			if (qty > stock) {
				throw new RuntimeException("Insufficient stock");
			}
			qty = -qty;
		}
		item.setRemainingStock(stock + qty);
		Inventory inventory = Inventory.builder().item(item).qty(request.getQty()).type(request.getType()).build();

		log.info("start to save db...");
		itemRepository.save(item);
		inventoryRepository.save(inventory);
		return convertToDto(inventory);
	}

	public InventoryDto findById(Long id) {
		Inventory inventory = inventoryRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException(id));
		return convertToDto(inventory);
	}

	@Transactional(readOnly = true)
	public Page<InventoryDto> getAll(Pageable pageable) {
		return inventoryRepository.findAll(pageable).map(inventory -> {
			Hibernate.initialize(inventory);
			return convertToDto(inventory);
		});
	}

	public void delete(Long id) {
		Inventory inventory = inventoryRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException(id));
		inventoryRepository.delete(inventory);
	}

	@Transactional
	public InventoryDto update(Long inventoryId, AddInventoryRequest request) {
		Inventory existingInventory = inventoryRepository.findById(inventoryId)
				.orElseThrow(() -> new DataNotFoundException(inventoryId));

		Item newItem = itemRepository.findById(request.getItemId())
				.orElseThrow(() -> new DataNotFoundException(request.getItemId()));

		if (!existingInventory.getItem().getId().equals(request.getItemId())) {
			Item oldItem = existingInventory.getItem();
			int oldStockAdjustment = existingInventory.getType().equalsIgnoreCase("W") ?
					existingInventory.getQty() :
					-existingInventory.getQty();

			oldItem.setRemainingStock(oldItem.getRemainingStock() + oldStockAdjustment);
			itemRepository.save(oldItem);
		}

		int newStockAdjustment = request.getType().equalsIgnoreCase("W") ? -request.getQty() : request.getQty();

		int newStock = newItem.getRemainingStock() + newStockAdjustment;
		if (newStock < 0)
			throw new RuntimeException("Insufficient stock");

		newItem.setRemainingStock(newStock);
		existingInventory.setQty(request.getQty());
		existingInventory.setType(request.getType());
		existingInventory.setItem(newItem);

		itemRepository.save(newItem);
		return convertToDto(inventoryRepository.save(existingInventory));
	}

	public InventoryDto convertToDto(Inventory inventory) {
		if (inventory == null)
			return null;

		return InventoryDto.builder()
				.item(itemService.convertItemToDto(inventory.getItem()))
				.type(inventory.getType())
				.qty(inventory.getQty())
				.build();
	}
}

