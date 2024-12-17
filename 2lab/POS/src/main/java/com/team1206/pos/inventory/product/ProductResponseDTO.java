package com.team1206.pos.inventory.product;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private UUID categoryId;
    private List<UUID> variationIds;
    private List<UUID> chargeIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}