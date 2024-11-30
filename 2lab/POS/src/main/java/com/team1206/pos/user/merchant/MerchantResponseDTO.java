package com.team1206.pos.user.merchant;

import lombok.Data;
import java.util.UUID;

@Data
public class MerchantResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String currency;
    private String city;
    private String country;
}
