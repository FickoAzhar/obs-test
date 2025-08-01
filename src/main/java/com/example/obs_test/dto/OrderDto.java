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
public class OrderDto {
	private String orderNo;
	private ItemDto item;
	private int qty;
	private BigDecimal price;
}
