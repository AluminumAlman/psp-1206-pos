package com.team1206.pos.payments.charge;

import com.team1206.pos.payments.charge.validation.OneOf;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/charges")
@OneOf
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
        return chargeService.handleGetChargesRequest(limit, offset, () -> chargeService.getCharges(limit, offset, merchantId));
    }

    @GetMapping
    @Operation(summary = "Get all charges by type")
    public ResponseEntity<Page<ChargeResponseDTO>> getChargesByType(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "chargeType") String chargeType) {
        return chargeService.handleGetChargesRequest(limit, offset, () -> chargeService.getCharges(limit, offset, chargeType));
    }

    @PostMapping
    @Operation(summary = "Create a new charge")
    public ResponseEntity<ChargeResponseDTO> createCharge(@Valid @RequestBody ChargeRequestDTO request) {
        ChargeResponseDTO response = chargeService.createCharge(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{chargeId}")
    @Operation(summary = "Retrieve charge by ID")
    public ResponseEntity<ChargeResponseDTO> getChargeById(@PathVariable UUID chargeId) {
        ChargeResponseDTO response = chargeService.getChargeById(chargeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chargeId}")
    @Operation(summary = "Update charge by ID")
    public ResponseEntity<ChargeResponseDTO> updateCharge(
            @PathVariable UUID chargeId,
            @Valid @RequestBody ChargeRequestDTO request) {
        ChargeResponseDTO response = chargeService.updateCharge(chargeId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{chargeId}")
    @Operation(summary = "Deactivate charge by ID")
    public ResponseEntity<Void> deactivateCharge(@PathVariable UUID chargeId) {
        chargeService.deactivateCharge(chargeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{chargeId}/reactivate")
    @Operation(summary = "Reactivate charge by ID")
    public ResponseEntity<ChargeResponseDTO> reactivateCharge(@PathVariable UUID chargeId) {
        ChargeResponseDTO response = chargeService.reactivateCharge(chargeId);
        return ResponseEntity.ok(response);
    }
}