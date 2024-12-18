package com.team1206.pos.order.orderItem;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderItemResponseDTO {
    private UUID id;
    private UUID productId;
    private UUID reservationId;
    private Integer quantity;
    private UUID productVariationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
