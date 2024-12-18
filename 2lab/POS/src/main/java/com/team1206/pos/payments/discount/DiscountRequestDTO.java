package com.team1206.pos.payments.discount;

import com.team1206.pos.common.validation.OneOf;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@OneOf(fields = {"percent", "amount"})
public class DiscountRequestDTO {
    @NotNull(message = "Discount name must not be null.")
    @NotBlank(message = "Discount name must not be blank.")
    @Size(max = 255, message = "Discount name size must not exceed 255.")
    private String name;

    @PositiveOrZero
    private Integer percent;

    @DecimalMin(value = "0.01", message = "Discount amount must be greater than or equal to 0.01")
    private BigDecimal amount;

    @NotNull(message = "Discount 'valid from' must not be null.")
    private LocalDateTime validFrom;

    @NotNull(message = "Discount 'valid until' must not be null.")
    private LocalDateTime validUntil;

    @NotNull(message = "Discount scope must not be null.")
    private Discount.Scope scope;
}
