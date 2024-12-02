package com.team1206.pos.user.merchant;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchants")
public class MerchantController {
    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping
    @Operation( summary = "Create a new merchant" )
    public ResponseEntity<MerchantResponseDTO> createMerchant(@Valid @RequestBody MerchantRequestDTO request) {
        MerchantResponseDTO createdMerchant = merchantService.createMerchant(request);
        return ResponseEntity.ok(createdMerchant);
    }

    @GetMapping
    @Operation( summary = "Get all merchants" )
    public ResponseEntity<List<MerchantResponseDTO>> getAllMerchants() {
        List<MerchantResponseDTO> merchants = merchantService.getAllMerchants();
        return ResponseEntity.ok(merchants);
    }

    @GetMapping("{merchantId}")
    @Operation( summary = "Retrieve merchant by ID" )
    public ResponseEntity<MerchantResponseDTO> getMerchant(@PathVariable UUID merchantId) {
        MerchantResponseDTO retrievedMerchant = merchantService.getMerchantById(merchantId);
        return ResponseEntity.ok(retrievedMerchant);
    }
}
