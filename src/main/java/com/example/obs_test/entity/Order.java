package com.example.obs_test.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, length = 20, updatable = false)
    @NotBlank(message = "Order number is required")
    private String orderNo;

    @NotNull(message = "Item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item"))
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Item item;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int qty;

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    private BigDecimal price;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
