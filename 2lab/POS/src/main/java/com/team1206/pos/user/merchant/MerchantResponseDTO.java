package com.team1206.pos.user.merchant;

import com.team1206.pos.common.dto.WorkHoursDTO;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
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
    private Map<DayOfWeek, WorkHoursDTO> schedule;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
