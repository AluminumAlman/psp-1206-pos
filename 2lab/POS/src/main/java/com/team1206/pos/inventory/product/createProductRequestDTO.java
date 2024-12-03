package com.team1206.pos.inventory.product;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

@Data
public class createProductRequestDTO {
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    private Integer price;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    // Empty array for no variations
    @NotNull(message = "Variations must not be null. Use an empty array for no variations.")
    private List<UUID> variationIds;

    // Empty list for no charges
    @NotNull(message = "Charges must not be null. Use an empty array for no charges.")
    private List<UUID> chargeIds;
}
