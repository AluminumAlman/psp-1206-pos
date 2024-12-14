package com.team1206.pos.inventory.productVariation;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class CreateProductVariationBodyDTO {

    @NotBlank(message = "Variation name is required")
    @Size(max = 100, message = "Variation name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    private BigDecimal price;
}
