package com.team1206.pos.payments.discount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DiscountResponseDTO {
    private UUID id;
    private String name;
    private Integer percent;
    private BigDecimal amount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private UUID merchantId;
}
