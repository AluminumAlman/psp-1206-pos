package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.validation.OneOf;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@OneOf(fields = {"percent", "amount"})
public class OrderChargeRequestDTO {
    @NotBlank
    @Pattern(regexp = "(?i)^(charge|discount)$", message = "chargeType must be either 'tax', 'charge', 'tip' or 'discount'")
    private String type;

    @NotBlank
    private String name;

    @Min(value = 0, message = "percent must be greater than or equal to 0")
    @Max(value = 100, message = "percent must be less than or equal to 100")
    private Integer percent;

    @DecimalMin(value = "0.01", message = "amount must be greater than or equal to 0.01")
    private BigDecimal amount;
}
