package com.example.obs_test.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
	private Long id;
	private String name;
	private BigDecimal price;
	private int remainingStock;
}
