package com.team1206.pos.payments.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionRequestDTO {
    @NotBlank
    @Pattern(regexp = "^(cash|payment_card|gift_card)$", message = "paymentMethod must be either 'cash', 'payment_card' or 'gift_card'")
    private String paymentMethodType;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than 0.01")
    private BigDecimal amount;
}
