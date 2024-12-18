package com.team1206.pos.inventory.inventoryLog;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryLogResponseDTO {
    private UUID id;
    private String type;
    private UUID product;
    private UUID productVariation;
    private UUID order;
    private Integer adjustment;
    private LocalDateTime createdAt;
}
