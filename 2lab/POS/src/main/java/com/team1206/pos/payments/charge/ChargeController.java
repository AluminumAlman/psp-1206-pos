package com.team1206.pos.payments.charge;

import com.team1206.pos.payments.charge.validation.OneOf;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    // TODO: Pakeisti į Pageable
    public ResponseEntity<List<ChargeResponseDTO>> getCharges() {
        List<ChargeResponseDTO> response = chargeService.getCharges();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new charge")
    public ResponseEntity<ChargeResponseDTO> createCharge(@Valid @RequestBody ChargeRequestDTO request) {
        ChargeResponseDTO response = chargeService.createCharge(request);
        return ResponseEntity.ok(response);
    }
}