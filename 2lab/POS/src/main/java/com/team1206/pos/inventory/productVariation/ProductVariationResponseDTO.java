package com.team1206.pos.inventory.productVariation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProductVariationResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
    private LocalDateTime createdAt;
}
