package com.team1206.pos.service.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ServiceRequestDTO {
    @NotBlank(message = "Service name is required.")
    @Size(max = 100, message = "Service name must not exceed 100 characters.")
    private String name;

    @NotNull(message = "Price is required.")
    @Min(value = 0, message = "Price must be greater than or equal to 0.")
    private BigDecimal price;

    @NotNull(message = "Duration is required.")
    @Min(value = 1, message = "Duration must be greater than or equal to 1.")
    private Long duration;

    @NotNull(message = "List of employees is required.")
    private List<UUID> employeeIds;

    @NotNull(message = "Merchant ID is required.")
    private UUID merchantId;
}
