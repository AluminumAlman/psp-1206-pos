package com.team1206.pos.payments.charge;

import com.team1206.pos.payments.charge.validation.OneOf;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/charges")
@OneOf
public class ChargeController {
    private final ChargeService chargeService;

    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @GetMapping
    @Operation(summary = "Get all charges")
    public ResponseEntity<Page<ChargeResponseDTO>> getCharges(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "chargeType") String chargeType) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        else if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }

        Page<ChargeResponseDTO> response = chargeService.getCharges(limit, offset, chargeType);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new charge")
    public ResponseEntity<ChargeResponseDTO> createCharge(@Valid @RequestBody ChargeRequestDTO request) {
        ChargeResponseDTO response = chargeService.createCharge(request);
        return ResponseEntity.ok(response);
    }
}