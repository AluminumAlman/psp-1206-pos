package com.team1206.pos.inventory.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class AdjustProductQuantityDTO {
    @NotNull(message = "Adjustment value is required")
    private Integer adjustment;
}
