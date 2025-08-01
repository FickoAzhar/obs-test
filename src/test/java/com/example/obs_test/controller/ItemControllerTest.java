package com.example.obs_test.controller;

import com.example.obs_test.dto.ItemDto;
import com.example.obs_test.dto.ItemRequest;
import com.example.obs_test.exception.DataNotFoundException;
import com.example.obs_test.exception.GlobalExceptionHandler;
import com.example.obs_test.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

	@Mock
	private ItemService itemService;

	@InjectMocks
	private ItemController itemController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();


	@BeforeEach
	void setup() {
		PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
		mockMvc = MockMvcBuilders.standaloneSetup(itemController)
				.setControllerAdvice(new GlobalExceptionHandler())
				.setCustomArgumentResolvers(pageableResolver)
				.build();
	}

	@Test
	@DisplayName("GET /items/{id} - Success")
	void getById_ShouldReturnItem() throws Exception {
		ItemDto mockItem = new ItemDto(1L, "Test Item", BigDecimal.valueOf(10000), 10);
		given(itemService.findById(1L)).willReturn(mockItem);

		mockMvc.perform(get("/items/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Test Item"));

		verify(itemService, times(1)).findById(1L);
	}

	@Test
	@DisplayName("POST /items - Success")
	public void createItem_ShouldReturnCreatedItem() throws Exception {
		ItemDto mockItem = new ItemDto(1L, "Test Item", BigDecimal.valueOf(10000), 10);
		ItemRequest itemRequest = new ItemRequest("keyboard", BigDecimal.TEN);
		given(itemService.save(any(ItemRequest.class))).willReturn(mockItem);

		mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L));

		verify(itemService, times(1)).save(any(ItemRequest.class));
	}

	@Test
	@DisplayName("PUT /items/{id} - Success")
	public void updateItem_ShouldReturnUpdatedItem() throws Exception {
		ItemDto mockItem = new ItemDto(1L, "Test Item", BigDecimal.valueOf(10000), 10);
		ItemRequest itemRequest = new ItemRequest("keyboard", BigDecimal.TEN);
		given(itemService.update(eq(1L), any(ItemRequest.class))).willReturn(mockItem);

		mockMvc.perform(put("/items/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Test Item"));

		verify(itemService, times(1)).update(eq(1L), any(ItemRequest.class));
	}

	@Test
	@DisplayName("DELETE /items/{id} - Success")
	public void deleteItem_ShouldReturnSuccessMessage() throws Exception {
		willDoNothing().given(itemService).delete(1L);

		mockMvc.perform(delete("/items/1"))
				.andExpect(status().isOk())
				.andExpect(content().string("Item with ID 1 deleted successfully"));

		verify(itemService, times(1)).delete(1L);
	}

	@Test
	@DisplayName("GET /items/{id} - Not Found")
	void getById_WhenItemNotFound_ShouldReturnNotFound() throws Exception {
		given(itemService.findById(anyLong()))
				.willThrow(new DataNotFoundException(999L));

		mockMvc.perform(get("/items/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Data not found with id: 999"));
	}

	@Test
	@DisplayName("POST /items - Bad Request")
	void createItem_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
		ItemRequest invalidRequest = new ItemRequest("", BigDecimal.ZERO); // Name kosong, price 0

		mockMvc.perform(post("/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}
}