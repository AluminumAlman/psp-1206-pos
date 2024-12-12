package com.team1206.pos.payments.charge;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ChargeResponseDTO {
    private UUID id;
    private String chargeType;
    private String chargeScope;
    private String name;
    private Integer percent;
    private BigDecimal amount;
    private UUID merchantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID[] products;
    private UUID[] services;
}