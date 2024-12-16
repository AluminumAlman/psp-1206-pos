package com.team1206.pos.payments.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionRequestDTO {
    @NotBlank
    private UUID orderId;

    @NotBlank
    @Pattern(regexp = "^(cash|payment_card|gift_card)$", message = "paymentMethod must be either 'cash', 'payment_card' or 'gift_card'")
    private String paymentMethod;

    @NotBlank
    private BigDecimal amount;
}
