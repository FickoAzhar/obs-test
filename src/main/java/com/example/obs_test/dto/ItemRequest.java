package com.example.obs_test.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 100, message = "Name must be 2-100 characters")
	private String name;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.01", message = "Price must be ≥ 0.01")
	private BigDecimal price;
}
