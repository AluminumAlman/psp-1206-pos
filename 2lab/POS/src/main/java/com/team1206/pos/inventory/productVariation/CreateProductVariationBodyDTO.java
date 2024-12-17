package com.team1206.pos.inventory.productVariation;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductVariationBodyDTO {

    @NotBlank(message = "Variation name is required")
    @Size(max = 100, message = "Variation name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be a non-negative value")
    private Integer quantity;
}
