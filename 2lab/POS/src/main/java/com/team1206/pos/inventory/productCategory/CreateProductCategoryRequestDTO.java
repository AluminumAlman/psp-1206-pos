package com.team1206.pos.inventory.productCategory;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateProductCategoryRequestDTO {
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Merchant ID is required")
    private UUID merchantId;
}
