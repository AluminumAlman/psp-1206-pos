package com.team1206.pos.inventory.product;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateProductRequestDTO {
    private String name;

    @Positive(message = "Price must be a positive value")
    private BigDecimal price;

    @PositiveOrZero(message = "Quantity must be a non-negative value")
    private Integer quantity;

    private UUID categoryId;
}
