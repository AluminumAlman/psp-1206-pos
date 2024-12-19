package com.team1206.pos.order.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderItemRequestDTO {
    @NotNull
    @Min(value = 1, message = "quantity must be greater than or equal to 1")
    private Integer quantity;
}
