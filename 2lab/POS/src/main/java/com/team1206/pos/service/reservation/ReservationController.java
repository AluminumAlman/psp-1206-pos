package com.team1206.pos.service.reservation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
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
        log.info("Received create new reservation request: {}", reservationRequestDTO);

        ReservationResponseDTO reservationResponseDTO = reservationService.createReservation(reservationRequestDTO);

        log.debug("Returning {} to create new reservation request");
        // TODO: add to response the URI to created discount.
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponseDTO);
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
        log.info("Received get reservation list request: limit={} offset={} serviceName={} customerName={} customerEmail={} customerPhone={} appointedAt={}",
                limit, offset, serviceName, customerName, customerEmail, customerPhone, appointedAt);

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }

        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }

        Page<ReservationResponseDTO> response = reservationService.getReservations(limit, offset, serviceName, customerName, customerEmail, customerPhone, appointedAt);

        log.debug("Returning {} to get reservation list request (limit={} offset={} service-name={} customer-name={} customer-email={} customer-phone={} appointedAt={})",
                response.stream().toList(), limit, offset, serviceName, customerName, customerEmail, customerPhone, appointedAt);
        return ResponseEntity.ok(response);
    }

    // PATCH: Update a reservation by ID
    @PatchMapping("/{reservationId}")
    @Operation(summary = "Update a reservation")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {
        log.info("Received update reservation request: reservationId={} {}", reservationId, reservationRequestDTO);

        ReservationResponseDTO reservationResponseDTO = reservationService.updateReservation(reservationId, reservationRequestDTO);

        log.debug("Returning {} to update reservation request (reservationId={})", reservationResponseDTO, reservationId);
        return ResponseEntity.ok(reservationResponseDTO);
    }

    // GET: Get reservation details by ID
    @GetMapping("/{reservationId}")
    @Operation(summary = "Get reservation details")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable UUID reservationId) {
        log.info("Received get reservation details request: reservationId={}", reservationId);

        ReservationResponseDTO reservationResponseDTO = reservationService.getReservationById(reservationId);

        log.debug("Returning {} to get reservation details request (reservationId={})", reservationResponseDTO, reservationId);
        return ResponseEntity.ok(reservationResponseDTO);
    }

    // DELETE: Cancel a reservation
    @DeleteMapping("/{reservationId}")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable UUID reservationId) {
        log.info("Received cancel reservation request: reservationId={}", reservationId);

        reservationService.cancelReservation(reservationId);

        log.debug("Returning nothing to cancel reservation request (reservationId={})", reservationId);
        return ResponseEntity.noContent().build();
    }
}