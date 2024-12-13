package com.team1206.pos.payments.discount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DiscountRequestDTO {
    @NotNull(message = "Discount name must not be null.")
    @NotBlank(message = "Discount name must not be blank.")
    @Size(max = 255, message = "Discount name size must not exceed 255.")
    private String name;

    private Integer percent;

    private Integer amount;

    @NotNull(message = "Discount merchant id must not be null.")
    private UUID merchantId;

    @NotNull(message = "Discount 'valid from' must not be null.")
    private LocalDateTime validFrom;

    @NotNull(message = "Discount 'valid until' must not be null.")
    private LocalDateTime validUntil;
}
