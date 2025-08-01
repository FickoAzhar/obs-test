package com.example.obs_test.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
	@NotNull(message = "Item must be specified")
	private Long itemId;

	@Min(value = 1, message = "Quantity must be at least 1")
	private int qty;
}
