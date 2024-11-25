package com.team1206.pos.user.merchant;

import lombok.Data;

@Data
public class MerchantRequestDTO {
    private String name;
    private String phone;
    private String email;
    private String currency;
    private String address;
    private String city;
    private String country;
    private String postcode;
}
