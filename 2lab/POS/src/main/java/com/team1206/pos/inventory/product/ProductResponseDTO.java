package com.team1206.pos.inventory.product;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private UUID id;                // Unique identifier for the product
    private String name;            // Product name
    private BigDecimal price;       // Product price in smallest currency unit (e.g., cents)
    private UUID categoryId;        // ID of the associated ProductCategory
    private List<UUID> variationIds; // List of associated ProductVariation IDs
    private List<UUID> chargeIds;   // List of associated Charge IDs
    private LocalDateTime createdAt; // Timestamp when the product was created
}