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
        // bus 201 kodas kaip ir dokumentacijoj
        // return ResponseEntity
        //            .created(URI.create("/merchants/" + createdMerchant.getId())) // You might want to return the URI of the newly created resource
        //            .body(createdMerchant);
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

    @PutMapping("{merchantId}")
    @Operation( summary = "Update merchant by ID" )
    public ResponseEntity<MerchantResponseDTO> updateMerchant(@PathVariable UUID merchantId, @Valid @RequestBody MerchantRequestDTO request) {
        MerchantResponseDTO updatedMerchant = merchantService.updateMerchantById(merchantId, request);
        return ResponseEntity.ok(updatedMerchant);
    }

    @DeleteMapping("{merchantId}")
    @Operation( summary = "Delete merchant by ID" )
    public ResponseEntity<Void> deleteMerchant(@PathVariable UUID merchantId) {
        merchantService.deleteMerchantById(merchantId);
        return ResponseEntity.noContent().build();
    }
}
