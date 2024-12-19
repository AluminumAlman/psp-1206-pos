package com.team1206.pos.user.user;

import com.team1206.pos.common.dto.WorkHoursDTO;
import com.team1206.pos.common.enums.UserRoles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.Map;

@Data
public class UserUpdateRequestDTO {
    @NotBlank(message = "First name is required")
    @Size(max = 40)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 40)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Size(max = 255)
    private String email;

    @NotNull(message = "A role must be assigned")
    private UserRoles role;

    @NotNull(message = "Weekly schedule is required")
    private Map<DayOfWeek, WorkHoursDTO> schedule;
}
