package com.team1206.pos.order.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequestDTO {
    @NotBlank
    @Valid
    private UUID merchantId;

    private List<UUID> orderItems;
}
