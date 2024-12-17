package com.team1206.pos.inventory.productVariation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductVariationBodyDTO {
    @Size(max = 100, message = "Variation name must not exceed 100 characters")
    private String name;

    @Positive(message = "Price must be a positive value")
    private BigDecimal price;

    @PositiveOrZero(message = "Quantity must be a non-negative value")
    private Integer quantity;
}
