package com.team1206.pos.inventory.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateProductRequestDTO {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    private Integer price;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    // Empty list for no charges
    @NotNull(message = "Charges must not be null. Use an empty array for no charges.")
    private List<UUID> chargeIds;
}
