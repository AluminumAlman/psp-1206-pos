package com.team1206.pos.service.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservationRequestDTO {
    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    @NotNull(message = "Appointed date and time is required")
    private LocalDateTime appointedAt;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Pattern(
            regexp = "^\\+\\d{10,15}$",
            message = "Phone number must start with '+' and be a valid international number"
    )    private String phone;
}
