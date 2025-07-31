package com.example.obs_test.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Item must be specified")
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;

    @NotBlank(message = "Type (T/W) is required")
    @Pattern(regexp = "T|W", message = "Type must be 'T' or 'W'")
    @Column(length = 1)
    private String type;
}


