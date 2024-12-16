package com.team1206.pos.payments.discount;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

// TODO: add authorization.
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
        List<DiscountResponseDTO> responses = discountService.getDiscounts();
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @Operation(summary = "Create new discount")
    public ResponseEntity<DiscountResponseDTO> createDiscount(
            HttpServletRequest request,
            @Valid @RequestBody CreateDiscountRequestDTO discountRequestDTO) throws Exception {
        DiscountResponseDTO response = discountService.createDiscount(discountRequestDTO);

        StringBuffer linkToResource = request.getRequestURL().append('/').append(response.getId());
        return ResponseEntity.created(new URI(linkToResource.toString())).body(response);
    }

    @GetMapping("{discountId}")
    @Operation(summary = "Retrieve discount")
    public ResponseEntity<DiscountResponseDTO> getDiscount(@PathVariable("discountId") UUID discountId) {
        DiscountResponseDTO response = discountService.getDiscount(discountId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{discountId}")
    @Operation(summary = "Update discount")
    public ResponseEntity<DiscountResponseDTO> updateDiscount(
            @PathVariable("discountId") UUID discountId,
            @Valid @RequestBody UpdateDiscountRequestDTO discountRequestDTO) {
        DiscountResponseDTO response = discountService.updateDiscount(discountId, discountRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{discountId}")
    @Operation(summary = "Delete a specific discount")
    public ResponseEntity<Void> deleteDiscount(@PathVariable("discountId") UUID discountId) {
        discountService.deleteDiscount(discountId);
        return ResponseEntity.noContent().build();
    }
}
