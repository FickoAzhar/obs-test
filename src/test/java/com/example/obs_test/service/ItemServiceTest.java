package com.example.obs_test.service;

import com.example.obs_test.dto.ItemDto;
import com.example.obs_test.dto.ItemRequest;
import com.example.obs_test.entity.Item;
import com.example.obs_test.exception.DataNotFoundException;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private ItemService itemService;

	private Item sampleItem;
	private ItemDto sampleItemDto;
	private ItemRequest sampleItemRequest;

	@BeforeEach
	void setUp() {
		sampleItem = Item.builder()
				.id(1L)
				.name("Laptop")
				.price(BigDecimal.valueOf(1000))
				.remainingStock(10)
				.build();

		sampleItemDto = ItemDto.builder()
				.id(1L)
				.name("Laptop")
				.price(BigDecimal.valueOf(1000))
				.remainingStock(10)
				.build();

		sampleItemRequest = new ItemRequest("Laptop", BigDecimal.valueOf(1000));
	}

	@Test
	@DisplayName("getAllItemsWithStock - Should return page of items")
	void getAllItemsWithStock_ShouldReturnPageOfItems() {
		// Arrange
		Page<Item> itemPage = new PageImpl<>(List.of(sampleItem));
		given(itemRepository.findAll(any(Pageable.class))).willReturn(itemPage);

		// Act
		Page<ItemDto> result = itemService.getAllItemsWithStock(Pageable.unpaged());

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals("Laptop", result.getContent().get(0).getName());

		verify(itemRepository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	@DisplayName("save - Should save and return item DTO")
	void save_ShouldSaveAndReturnItemDto() {
		// Arrange
		given(itemRepository.save(any(Item.class))).willReturn(sampleItem);

		// Act
		ItemDto result = itemService.save(sampleItemRequest);

		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Laptop", result.getName());

		verify(itemRepository, times(1)).save(any(Item.class));
	}

	@Test
	@DisplayName("findById - Should return item when exists")
	void findById_WhenItemExists_ShouldReturnItem() {
		// Arrange
		given(itemRepository.findById(1L)).willReturn(Optional.of(sampleItem));

		// Act
		ItemDto result = itemService.findById(1L);

		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Laptop", result.getName());

		verify(itemRepository, times(1)).findById(1L);
	}

	@Test
	@DisplayName("findById - Should throw exception when item not found")
	void findById_WhenItemNotExists_ShouldThrowException() {
		// Arrange
		given(itemRepository.findById(anyLong())).willReturn(Optional.empty());

		// Act & Assert
		assertThrows(DataNotFoundException.class, () -> itemService.findById(1L));

		verify(itemRepository, times(1)).findById(1L);
	}

	@Test
	@DisplayName("update - Should update and return updated item")
	void update_ShouldUpdateAndReturnItem() {
		// Arrange
		ItemRequest updateRequest = new ItemRequest("Updated Laptop", BigDecimal.valueOf(1200));
		given(itemRepository.findById(1L)).willReturn(Optional.of(sampleItem));
		given(itemRepository.save(any(Item.class))).willAnswer(invocation -> invocation.getArgument(0));

		// Act
		ItemDto result = itemService.update(1L, updateRequest);

		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("Updated Laptop", result.getName());
		assertEquals(BigDecimal.valueOf(1200), result.getPrice());

		verify(itemRepository, times(1)).findById(1L);
		verify(itemRepository, times(1)).save(any(Item.class));
	}

	@Test
	@DisplayName("update - Should throw exception when item not found")
	void update_WhenItemNotExists_ShouldThrowException() {
		// Arrange
		given(itemRepository.findById(anyLong())).willReturn(Optional.empty());

		// Act & Assert
		assertThrows(DataNotFoundException.class,
				() -> itemService.update(1L, sampleItemRequest));

		verify(itemRepository, times(1)).findById(1L);
		verify(itemRepository, never()).save(any());
	}

	@Test
	@DisplayName("delete - Should delete item when exists")
	void delete_WhenItemExists_ShouldDeleteItem() {
		// Arrange
		given(itemRepository.findById(1L)).willReturn(Optional.of(sampleItem));
		willDoNothing().given(itemRepository).delete(any(Item.class));

		// Act
		itemService.delete(1L);

		// Assert
		verify(itemRepository, times(1)).findById(1L);
		verify(itemRepository, times(1)).delete(sampleItem);
	}

	@Test
	@DisplayName("delete - Should throw exception when item not found")
	void delete_WhenItemNotExists_ShouldThrowException() {
		// Arrange
		given(itemRepository.findById(anyLong())).willReturn(Optional.empty());

		// Act & Assert
		assertThrows(DataNotFoundException.class,
				() -> itemService.delete(1L));

		verify(itemRepository, times(1)).findById(1L);
		verify(itemRepository, never()).delete(any());
	}

	@Test
	@DisplayName("convertItemToDto - Should return null when input is null")
	void convertItemToDto_WhenInputIsNull_ShouldReturnNull() {
		// Act
		ItemDto result = itemService.convertItemToDto(null);

		// Assert
		assertNull(result);
	}

	@Test
	@DisplayName("convertItemToDto - Should convert item to DTO")
	void convertItemToDto_ShouldConvertItemToDto() {
		// Act
		ItemDto result = itemService.convertItemToDto(sampleItem);

		// Assert
		assertNotNull(result);
		assertEquals(sampleItem.getId(), result.getId());
		assertEquals(sampleItem.getName(), result.getName());
		assertEquals(sampleItem.getPrice(), result.getPrice());
		assertEquals(sampleItem.getRemainingStock(), result.getRemainingStock());
	}
}
