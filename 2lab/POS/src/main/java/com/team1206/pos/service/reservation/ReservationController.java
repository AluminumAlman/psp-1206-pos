package com.team1206.pos.service.reservation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // POST: Create a new reservation
    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationRequestDTO));
    }

    // GET: Get reservation list
    @GetMapping
    @Operation(summary = "Get reservation list")
    public ResponseEntity<Page<ReservationResponseDTO>> getReservations(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "service-name", required = false) String serviceName,
            @RequestParam(value = "customer-name", required = false) String customerName,
            @RequestParam(value = "customer-email", required = false) String customerEmail,
            @RequestParam(value = "customer-phone", required = false) String customerPhone,
            @RequestParam(value = "appointedAt", required = false) LocalDateTime appointedAt) {

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }

        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }

        return ResponseEntity.ok(reservationService.getReservations(limit, offset, serviceName, customerName, customerEmail, customerPhone, appointedAt));
    }

    // PATCH: Update a reservation by ID
    @PatchMapping("/{reservationId}")
    @Operation(summary = "Update a reservation")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {
        return ResponseEntity.ok(reservationService.updateReservation(reservationId, reservationRequestDTO));
    }

    // GET: Get reservation details by ID
    @GetMapping("/{reservationId}")
    @Operation(summary = "Get reservation details")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(reservationService.getReservationById(reservationId));
    }

    // DELETE: Cancel a reservation
    @DeleteMapping("/{reservationId}")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable UUID reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    // GET: Get available reservation slots
    @GetMapping("/availableSlots")
    @Operation(summary = "Get available reservation slots")
    public ResponseEntity<AvailableSlotsResponseDTO> getAvailableSlots(
            @RequestParam(value = "date") LocalDate date,
            @RequestParam(value = "serviceId") UUID serviceId) {
        return ResponseEntity.ok(reservationService.getAvailableSlots(date, serviceId));
    }
}