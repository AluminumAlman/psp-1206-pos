package com.team1206.pos.payments.charge;

import com.team1206.pos.common.validation.OneOf;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/charges")
@OneOf(fields = {"percent", "amount"})
public class ChargeController {
    private final ChargeService chargeService;

    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @GetMapping("/merchant")
    @Operation(summary = "Get all charges by merchant ID")
    public ResponseEntity<Page<ChargeResponseDTO>> getChargesByMerchant(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "merchantId") UUID merchantId) {
        log.info("Received get all charges request: limit={} offset={} merchantId={}", limit, offset, merchantId);

        ResponseEntity<Page<ChargeResponseDTO>> response = chargeService.handleGetChargesRequest(limit, offset, () -> chargeService.getCharges(limit, offset, merchantId));

        log.debug("Returning {} to get all charges request (limit={} offset={} merchantId={})",
                response.getBody().stream().toList(), limit, offset, merchantId);
        return response;
    }

    @GetMapping
    @Operation(summary = "Get all charges by type")
    public ResponseEntity<Page<ChargeResponseDTO>> getChargesByType(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "chargeType") String chargeType) {
        log.info("Received get all charges request: limit={} offset={} chargeType={}", limit, offset, chargeType);

        ResponseEntity<Page<ChargeResponseDTO>> response = chargeService.handleGetChargesRequest(limit, offset, () -> chargeService.getCharges(limit, offset, chargeType));

        log.debug("Returning {} to get all charges request (limit={} offset={} chargeType={})", response.getBody().stream().toList(), limit, offset, chargeType);
        return response;
    }

    @PostMapping
    @Operation(summary = "Create a new charge")
    public ResponseEntity<ChargeResponseDTO> createCharge(@Valid @RequestBody ChargeRequestDTO request) {
        log.info("Received create charge request: {}", request);

        ChargeResponseDTO response = chargeService.createCharge(request);

        log.debug("Returning {} to create charge request", response);
        // TODO: add to response the URI to created charge.
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{chargeId}")
    @Operation(summary = "Retrieve charge by ID")
    public ResponseEntity<ChargeResponseDTO> getChargeById(@PathVariable UUID chargeId) {
        log.info("Received get charge request: chargeId={}", chargeId);

        ChargeResponseDTO response = chargeService.getChargeById(chargeId);

        log.debug("Returning {} to get charge request (chargeId={})", response, chargeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chargeId}")
    @Operation(summary = "Update charge by ID")
    public ResponseEntity<ChargeResponseDTO> updateCharge(
            @PathVariable UUID chargeId,
            @Valid @RequestBody ChargeRequestDTO request) {
        log.info("Received update charge request: chargeId={} {}", chargeId, request);

        ChargeResponseDTO response = chargeService.updateCharge(chargeId, request);

        log.debug("Returning {} to update charge request (chargeId={})", response, chargeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{chargeId}")
    @Operation(summary = "Deactivate charge by ID")
    public ResponseEntity<Void> deactivateCharge(@PathVariable UUID chargeId) {
        log.info("Received deactivate charge request: chargeId={}", chargeId);

        chargeService.deactivateCharge(chargeId);

        log.debug("Returning nothing to deactivate charge request (chargeId={})", chargeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{chargeId}/reactivate")
    @Operation(summary = "Reactivate charge by ID")
    public ResponseEntity<ChargeResponseDTO> reactivateCharge(@PathVariable UUID chargeId) {
        log.info("Received reactivate charge request: chargeId={}", chargeId);

        ChargeResponseDTO response = chargeService.reactivateCharge(chargeId);

        log.debug("Returning {} to reactivate charge request (chargeId={})", response, chargeId);
        return ResponseEntity.ok(response);
    }
}