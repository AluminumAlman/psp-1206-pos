package com.team1206.pos.payments.discount;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: add authorization.
@Slf4j
@RestController
@RequestMapping("/discounts")
public class DiscountController {
    private final DiscountService discountService;
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping
    @Operation(summary = "Get discount list")
    public ResponseEntity<List<DiscountResponseDTO>> getDiscounts() {
        log.info("Received get discounts request");

        List<DiscountResponseDTO> responses = discountService.getDiscounts();

        log.debug("Returning {} to get discounts request", responses);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Operation(summary = "Create new discount")
    public ResponseEntity<DiscountResponseDTO> createDiscount(
            HttpServletRequest request,
            @Valid @RequestBody DiscountRequestDTO discountRequestDTO) {
        log.info("Received create discount request: {}", discountRequestDTO);

        DiscountResponseDTO response = discountService.createDiscount(discountRequestDTO);

        log.debug("Returning {} to create discount request", response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{discountId}")
    @Operation(summary = "Retrieve discount")
    public ResponseEntity<DiscountResponseDTO> getDiscount(@PathVariable("discountId") UUID discountId) {
        log.info("Received get discount request: discountId={}", discountId);

        DiscountResponseDTO response = discountService.getDiscount(discountId);

        log.debug("Returning {} to get discount request (discountId={})", response, discountId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{discountId}")
    @Operation(summary = "Update discount")
    public ResponseEntity<DiscountResponseDTO> updateDiscount(
            @PathVariable("discountId") UUID discountId,
            @Valid @RequestBody DiscountRequestDTO discountRequestDTO) {
        log.info("Received update discount request: discountId={} {}", discountId, discountRequestDTO);

        DiscountResponseDTO response = discountService.updateDiscount(discountId, discountRequestDTO);

        log.debug("Returning {} to update discount request (discountId={})", response, discountId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{discountId}")
    @Operation(summary = "Delete a specific discount")
    public ResponseEntity<Void> deleteDiscount(@PathVariable("discountId") UUID discountId) {
        log.info("Received delete discount request: discountId={}", discountId);

        discountService.deleteDiscount(discountId);

        log.debug("Returning nothing to delete discount request (discountId={})", discountId);
        return ResponseEntity.noContent().build();
    }
}
