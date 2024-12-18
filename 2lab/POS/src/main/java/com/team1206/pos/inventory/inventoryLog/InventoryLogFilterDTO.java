package com.team1206.pos.inventory.inventoryLog;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryLogFilterDTO {
    private UUID productId = null;
    private UUID productVariationId = null;
    private UUID orderId = null;
    private UUID userId = null;
}
