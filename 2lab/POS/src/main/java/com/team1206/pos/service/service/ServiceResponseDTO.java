package com.team1206.pos.service.service;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ServiceResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal price;
    private Long duration;
    private UUID merchantId;
    private LocalDateTime createdAt;
}
