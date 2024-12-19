package com.team1206.pos.user.merchant;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
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
        log.info("Received create merchant request: {}", request);

        MerchantResponseDTO createdMerchant = merchantService.createMerchant(request);

        log.debug("Returning {} to create merchant request", createdMerchant);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMerchant);
        // return ResponseEntity
        //            .created(URI.create("/merchants/" + createdMerchant.getId())) // You might want to return the URI of the newly created resource
        //            .body(createdMerchant);
    }

    @GetMapping
    @Operation( summary = "Get all merchants" )
    public ResponseEntity<List<MerchantResponseDTO>> getAllMerchants() {
        log.info("Received get all merchants request");

        List<MerchantResponseDTO> merchants = merchantService.getAllMerchants();

        log.debug("Returning {} to get all merchants request", merchants);
        return ResponseEntity.ok(merchants);
    }

    @GetMapping("{merchantId}")
    @Operation( summary = "Retrieve merchant by ID" )
    public ResponseEntity<MerchantResponseDTO> getMerchant(@PathVariable UUID merchantId) {
        log.info("Received get merchant request: merchantId={}", merchantId);

        MerchantResponseDTO retrievedMerchant = merchantService.getMerchantById(merchantId);

        log.debug("Returning {} to get merchant request (merchantId={})", retrievedMerchant, merchantId);
        return ResponseEntity.ok(retrievedMerchant);
    }

    @PutMapping("{merchantId}")
    @Operation( summary = "Update merchant by ID" )
    public ResponseEntity<MerchantResponseDTO> updateMerchant(@PathVariable UUID merchantId, @Valid @RequestBody MerchantRequestDTO request) {
        log.info("Received update merchant request: merchantId={} {}", merchantId, request);

        MerchantResponseDTO updatedMerchant = merchantService.updateMerchantById(merchantId, request);

        log.debug("Returning {} to update merchant request (merchantId={})", updatedMerchant, merchantId);
        return ResponseEntity.ok(updatedMerchant);
    }

    @DeleteMapping("{merchantId}")
    @Operation( summary = "Delete merchant by ID" )
    public ResponseEntity<Void> deleteMerchant(@PathVariable UUID merchantId) {
        log.info("Received delete merchant request: merchantId={}", merchantId);

        merchantService.deleteMerchantById(merchantId);

        log.debug("Returning nothing to delete merchant request (merchantId={})", merchantId);
        return ResponseEntity.noContent().build();
    }
}
