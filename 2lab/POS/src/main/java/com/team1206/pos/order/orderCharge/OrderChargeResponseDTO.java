package com.team1206.pos.order.orderCharge;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderChargeResponseDTO {
    private UUID id;
    private String type;
    private String name;
    private Integer percent;
    private BigDecimal amount;
    private UUID merchantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
