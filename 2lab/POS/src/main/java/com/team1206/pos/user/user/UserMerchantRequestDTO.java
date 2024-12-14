package com.team1206.pos.user.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserMerchantRequestDTO {
    @NotNull(message = "Merchant ID is required")
    private UUID merchantId;
}
