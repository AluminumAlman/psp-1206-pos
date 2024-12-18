package com.team1206.pos.order.order;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponseDTO {
    private UUID id;
    private String status;
    private List<UUID> charges;
    private List<UUID> items;
    private List<UUID> transactions;
    private UUID merchantId;
    private List<UUID> discounts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
