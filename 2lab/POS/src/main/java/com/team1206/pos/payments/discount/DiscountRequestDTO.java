package com.team1206.pos.payments.discount;

import com.team1206.pos.common.enums.DiscountScope;
import com.team1206.pos.common.validation.OneOf;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@OneOf(fields = {"percent", "amount"})
@Data
public class DiscountRequestDTO {
    @NotNull(message = "Discount name must not be null.")
    @NotBlank(message = "Discount name must not be blank.")
    @Size(max = 255, message = "Discount name size must not exceed 255.")
    private String name;

    @Min(value = 0, message = "Discount percentage may not be below 0")
    @Max(value = 100, message = "Discount percentage may not be over 100")
    private Integer percent;

    @DecimalMin(value = "0.01", message = "Discount amount must not be under 0.01")
    private BigDecimal amount;

    @NotNull(message = "Discount 'valid from' must not be null.")
    private LocalDateTime validFrom;

    @NotNull(message = "Discount 'valid until' must not be null.")
    private LocalDateTime validUntil;

    @NotNull(message = "Discount scope must not be null.")
    private DiscountScope scope;
}
