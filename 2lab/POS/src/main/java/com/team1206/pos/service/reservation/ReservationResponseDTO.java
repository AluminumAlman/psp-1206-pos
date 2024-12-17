package com.team1206.pos.service.reservation;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservationResponseDTO {
    private UUID id;
    private UUID serviceId;
    private String serviceName;
    private UUID employeeId;
    private String employeeFullName;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDateTime appointedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
