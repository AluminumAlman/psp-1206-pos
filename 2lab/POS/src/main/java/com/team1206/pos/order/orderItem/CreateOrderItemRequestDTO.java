package com.team1206.pos.order.orderItem;

import com.team1206.pos.common.validation.OneOf;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@OneOf(fields = {"productId", "reservationId"})
public class CreateOrderItemRequestDTO {
    private UUID productId;
    private UUID reservationId;

    private UUID productVariationId;

    @Min(value = 1, message = "quantity must be greater than or equal to 1")
    private Integer quantity;
}
