package com.team1206.pos.payments.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponseDTO {
    private UUID id;
    private String status;
    private String paymentMethodType;
    private BigDecimal amount;
    private UUID orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
