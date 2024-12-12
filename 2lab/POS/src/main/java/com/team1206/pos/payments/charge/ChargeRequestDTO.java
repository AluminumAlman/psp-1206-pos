package com.team1206.pos.payments.charge;

import com.team1206.pos.payments.charge.validation.OneOf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@OneOf
public class ChargeRequestDTO {
    @NotBlank
    @Pattern(regexp = "^(tax|service)$", message = "chargeType must be either 'tax' or 'service'")
    private String chargeType;

    @NotBlank
    @Pattern(regexp = "^(product|order)$", message = "chargeScope must be either 'product' or 'order'")
    private String chargeScope;

    @NotBlank
    private String name;

    private Integer percent;

    private BigDecimal amount;

    private List<UUID> products;

    private List<UUID> services;

    private UUID merchantId;
}