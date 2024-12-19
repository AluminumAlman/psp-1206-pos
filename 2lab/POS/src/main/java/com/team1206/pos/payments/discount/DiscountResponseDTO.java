package com.team1206.pos.payments.discount;

import com.team1206.pos.common.enums.DiscountScope;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private DiscountScope scope;
    private List<UUID> serviceIds;
    private List<UUID> productCategoryIds;
    private List<UUID> productIds;
    private List<UUID> productVariationIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
