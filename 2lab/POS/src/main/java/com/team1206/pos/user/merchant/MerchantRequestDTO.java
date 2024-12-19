package com.team1206.pos.user.merchant;

import com.team1206.pos.common.dto.WorkHoursDTO;
import lombok.Data;

import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.util.Map;

@Data
public class MerchantRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(
            regexp = "^\\+\\d{10,15}$",
            message = "Phone number must start with '+' and be a valid international number"
    )
    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid ISO 4217 code (e.g., USD, EUR, GBP)")
    @NotBlank(message = "Currency is required")
    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    @NotBlank(message = "Postcode is required")
    @Size(max = 20, message = "Postcode must not exceed 20 characters")
    private String postcode;

    @NotNull(message = "Schedule is required")
    private Map<DayOfWeek, WorkHoursDTO> schedule;
}
