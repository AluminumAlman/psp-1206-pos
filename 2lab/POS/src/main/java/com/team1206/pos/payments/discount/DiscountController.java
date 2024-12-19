package com.team1206.pos.payments.discount;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// TODO: add authorization.git
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
    public ResponseEntity<Page<DiscountResponseDTO>> getDiscounts(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "validOnly", defaultValue = "true") boolean validOnly
    ) {
        log.info("Received get discounts request: limit={} offset={} validOnly={}", limit, offset, validOnly);

        if (limit <= 0)
            throw new IllegalArgumentException("Limit must be at least 1");
        if (offset < 0)
            throw new IllegalArgumentException("Offset must be at least 0");

        Page<DiscountResponseDTO> responses = discountService.getDiscounts(limit, offset, validOnly);

        log.debug("Returning {} to get discounts request (limit={} offset={} validOnly={})",
                responses.stream().toList(), limit, offset, validOnly);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Operation(summary = "Create new discount")
    public ResponseEntity<DiscountResponseDTO> createDiscount(
            HttpServletRequest request,
            @Valid @RequestBody DiscountRequestDTO discountRequestDTO) throws Exception {
        log.info("Received create discount request: {}", discountRequestDTO);

        DiscountResponseDTO response = discountService.createDiscount(discountRequestDTO);

        log.debug("Returning {} to create discount request", response);
        // TODO: add to response the URI to created discount.
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
