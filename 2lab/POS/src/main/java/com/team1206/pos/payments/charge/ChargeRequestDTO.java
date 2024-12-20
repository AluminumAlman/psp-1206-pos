package com.team1206.pos.payments.charge;

import com.team1206.pos.common.validation.OneOf;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@OneOf(fields = {"percent", "amount"})
public class ChargeRequestDTO {
    @NotBlank
    @Pattern(regexp = "(?i)^(tax|service)$", message = "chargeType must be either 'tax' or 'service'")
    private String chargeType;

    @NotBlank
    private String name;

    @Min(value = 0, message = "percent must be greater than or equal to 0")
    @Max(value = 100, message = "percent must be less than or equal to 100")
    private Integer percent;

    @DecimalMin(value = "0.01", message = "amount must be greater than or equal to 0.01")
    private BigDecimal amount;

    @NotNull(message = "products must not be null; pass an empty array instead")
    private List<UUID> products;

    @NotNull(message = "services must not be null; pass an empty array instead")
    private List<UUID> services;
}