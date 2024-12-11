package com.team1206.pos.inventory.productCategory;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProductCategoryResponseDTO {
    private UUID id;
    private String name;
    private UUID merchantId;
    private LocalDateTime createdAt;
}
