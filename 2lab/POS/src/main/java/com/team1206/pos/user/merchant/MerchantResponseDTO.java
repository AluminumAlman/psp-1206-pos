package com.team1206.pos.user.merchant;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MerchantResponseDTO {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String currency;
    private String address;
    private String city;
    private String country;
    private String postcode;
    private LocalDateTime createdAt;
}
