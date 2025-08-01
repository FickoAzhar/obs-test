package com.example.obs_test.controller;

import com.example.obs_test.dto.AddInventoryRequest;
import com.example.obs_test.dto.InventoryDto;
import com.example.obs_test.dto.ItemDto;
import com.example.obs_test.exception.DataNotFoundException;
import com.example.obs_test.exception.GlobalExceptionHandler;
import com.example.obs_test.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

	@Mock
	private InventoryService inventoryService;

	@InjectMocks
	private InventoryController inventoryController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(inventoryController)
				.setControllerAdvice(new GlobalExceptionHandler()) // Jika ada
				.build();
	}

	@Test
	@DisplayName("GET /inventory/{id} - Success")
	void getById_ShouldReturnInventory() throws Exception {
		// Arrange
		ItemDto itemDto = new ItemDto(1L, "mouse", BigDecimal.TEN, 2);
		InventoryDto mockInventory = new InventoryDto(itemDto, 10, "T");
		given(inventoryService.findById(1L)).willReturn(mockInventory);

		// Act & Assert
		mockMvc.perform(get("/inventory/1"))
				.andExpect(status().isOk());

		verify(inventoryService, times(1)).findById(1L);
	}


	@Test
	@DisplayName("POST /inventory - Success")
	void addInventory_ShouldReturnCreatedInventory() throws Exception {
		// Arrange
		AddInventoryRequest request = new AddInventoryRequest(1L, 10, "T");
		ItemDto itemDto = new ItemDto(1L, "mouse", BigDecimal.TEN, 2);
		InventoryDto response = new InventoryDto(itemDto, 10, "T");
		given(inventoryService.save(any(AddInventoryRequest.class))).willReturn(response);

		// Act & Assert
		mockMvc.perform(post("/inventory")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.qty").value(10));

		verify(inventoryService, times(1)).save(any(AddInventoryRequest.class));
	}

	@Test
	@DisplayName("PUT /inventory/{id} - Success")
	void updateInventory_ShouldReturnUpdatedInventory() throws Exception {
		// Arrange
		AddInventoryRequest request = new AddInventoryRequest(1L, 15, "T");
		ItemDto itemDto = new ItemDto(1L, "mouse", BigDecimal.TEN, 2);
		InventoryDto response = new InventoryDto(itemDto, 15, "T");
		given(inventoryService.update(eq(1L), any(AddInventoryRequest.class))).willReturn(response);

		// Act & Assert
		mockMvc.perform(put("/inventory/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.qty").value(15))
				.andExpect(jsonPath("$.type").value("T"));

		verify(inventoryService, times(1)).update(eq(1L), any(AddInventoryRequest.class));
	}

	@Test
	@DisplayName("DELETE /inventory/{id} - Success")
	void deleteInventory_ShouldReturnSuccessMessage() throws Exception {
		// Arrange
		willDoNothing().given(inventoryService).delete(1L);

		// Act & Assert
		mockMvc.perform(delete("/inventory/1"))
				.andExpect(status().isOk())
				.andExpect(content().string("Inventory with ID 1 deleted successfully"));

		verify(inventoryService, times(1)).delete(1L);
	}

	@Test
	@DisplayName("GET /inventory/{id} - Not Found")
	void getById_WhenNotFound_ShouldReturnNotFound() throws Exception {
		// Arrange
		given(inventoryService.findById(anyLong())).willThrow(new DataNotFoundException(999L));

		// Act & Assert
		mockMvc.perform(get("/inventory/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Data not found with id: 999"));

		verify(inventoryService, times(1)).findById(999L);
	}

	@Test
	@DisplayName("POST /inventory - Bad Request")
	void addInventory_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
		AddInventoryRequest invalidRequest = new AddInventoryRequest(null, -1, "");

		mockMvc.perform(post("/inventory")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

}