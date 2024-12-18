package com.team1206.pos.inventory.inventoryLog;

import com.team1206.pos.common.validation.OneOf;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@OneOf(fields = {"product", "productVariation"})
@Data
public class CreateInventoryLogRequestDTO {
    private UUID product;

    private UUID productVariation;

    private UUID order;

    @NotNull(message = "Adjustment value is required")
    private Integer adjustment;

}
