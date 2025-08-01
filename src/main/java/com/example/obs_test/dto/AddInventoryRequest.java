package com.example.obs_test.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddInventoryRequest {

    @NotNull(message = "Item must be specified")
    private Long itemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;

    @NotBlank(message = "Type (T/W) is required")
    @Pattern(regexp = "T|W", message = "Type must be 'T' or 'W'")
    @Column(length = 1)
    private String type;
}
