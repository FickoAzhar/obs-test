package com.example.obs_test.controller;

import com.example.obs_test.dto.ItemDto;
import com.example.obs_test.dto.OrderDto;
import com.example.obs_test.dto.OrderRequest;
import com.example.obs_test.exception.GlobalExceptionHandler;
import com.example.obs_test.service.OrderService;
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
class OrderControllerTest {

	@Mock
	private OrderService orderService;

	@InjectMocks
	private OrderController orderController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(orderController)
				.setControllerAdvice(new GlobalExceptionHandler()) // Jika ada exception handler
				.build();
	}

	@Test
	@DisplayName("GET /orders/{orderNo} - Success")
	void getByOrderNo_ShouldReturnOrder() throws Exception {
		// Arrange
		ItemDto mockItem = new ItemDto(1L, "Test Item", BigDecimal.valueOf(10000), 10);
		OrderDto mockOrder = OrderDto.builder()
				.orderNo("ORD-123")
				.item(mockItem)
				.qty(1)
				.price(BigDecimal.valueOf(1000))
				.build();

		given(orderService.findByOrderNo("ORD-123")).willReturn(mockOrder);

		// Act & Assert
		mockMvc.perform(get("/orders/ORD-123"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderNo").value("ORD-123"));

		verify(orderService, times(1)).findByOrderNo("ORD-123");
	}

	@Test
	@DisplayName("POST /orders - Success")
	void placeOrder_ShouldReturnCreatedOrder() throws Exception {
		// Arrange
		ItemDto mockItem = new ItemDto(1L, "Test Item", BigDecimal.valueOf(10000), 10);
		OrderRequest request = new OrderRequest(1L, 2);
		OrderDto response = OrderDto.builder()
				.orderNo("ORD-456")
				.item(mockItem)
				.qty(2)
				.price(BigDecimal.valueOf(50))
				.build();

		given(orderService.placeOrder(any(OrderRequest.class))).willReturn(response);

		// Act & Assert
		mockMvc.perform(post("/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderNo").value("ORD-456"))
				.andExpect(jsonPath("$.qty").value(2));

		verify(orderService, times(1)).placeOrder(any(OrderRequest.class));
	}

	@Test
	@DisplayName("PUT /orders/{orderNo} - Success")
	void updateOrder_ShouldReturnUpdatedOrder() throws Exception {
		// Arrange
		OrderRequest request = new OrderRequest(1L, 3);
		ItemDto mockItem = new ItemDto(1L, "Test Item", BigDecimal.valueOf(10000), 10);
		OrderDto response = OrderDto.builder()
				.orderNo("ORD-789")
				.item(mockItem)
				.qty(3)
				.price(BigDecimal.valueOf(75))
				.build();

		given(orderService.update(eq("ORD-789"), any(OrderRequest.class))).willReturn(response);

		// Act & Assert
		mockMvc.perform(put("/orders/ORD-789")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderNo").value("ORD-789"))
				.andExpect(jsonPath("$.qty").value(3));

		verify(orderService, times(1)).update(eq("ORD-789"), any(OrderRequest.class));
	}

	@Test
	@DisplayName("DELETE /orders/{orderNo} - Success")
	void deleteOrder_ShouldReturnSuccessMessage() throws Exception {
		// Arrange
		willDoNothing().given(orderService).delete("ORD-123");

		// Act & Assert
		mockMvc.perform(delete("/orders/ORD-123"))
				.andExpect(status().isOk())
				.andExpect(content().string("order No ORD-123 deleted successfully"));

		verify(orderService, times(1)).delete("ORD-123");
	}

	@Test
	@DisplayName("POST /orders - Bad Request")
	void placeOrder_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
		// Arrange - Invalid request (itemId blank, qty 0)
		OrderRequest invalidRequest = new OrderRequest(1L, 0);

		// Act & Assert
		mockMvc.perform(post("/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}
}
