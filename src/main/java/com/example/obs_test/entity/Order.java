package com.example.obs_test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`order`")
public class Order {

    @Id
    @NotBlank(message = "Order number is required")
    private String orderNo;

    @NotNull(message = "Item is required")
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;

    private int price;
}

