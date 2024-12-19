package com.team1206.pos.user.user;

import com.team1206.pos.common.dto.WorkHoursDTO;
import com.team1206.pos.common.enums.UserRoles;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class UserResponseDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private UUID merchantId;
    private UserRoles role;
    private Map<DayOfWeek, WorkHoursDTO> schedule;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
