package com.team1206.pos.inventory.productVariation;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductVariationController {
    private final ProductVariationService productVariationService;
    public ProductVariationController(ProductVariationService productVariationService) {
        this.productVariationService = productVariationService;
    }

    @Operation(summary = "Create new product variation")
    @PostMapping("/{productId}/variations")
    public ResponseEntity<ProductVariationResponseDTO> createProductVariation(
            @PathVariable String productId,
            @Valid @RequestBody CreateProductVariationBodyDTO requestBody
    ) {
        ProductVariationResponseDTO createdVariation =
                productVariationService.createProductVariation(UUID.fromString(productId), requestBody);

        return ResponseEntity.ok(createdVariation);
    }

    @Operation(summary = "Get product's variation by ID")
    @GetMapping("/{productId}/variations/{variationId}")
    public ResponseEntity<ProductVariationResponseDTO> getProductVariation(
            @PathVariable String productId,
            @PathVariable String variationId
    ) {

        // TODO Ar verta tikrinti, ar variacija atitinka productId is url ir ar tas produktas egzistuoja
        ProductVariationResponseDTO variation = productVariationService.getProductVariationById(UUID.fromString(variationId));

        return ResponseEntity.ok(variation);
    }

    @Operation(summary = "Get all product's variations")
    @GetMapping("/{productId}/variations")
    public ResponseEntity<List<ProductVariationResponseDTO>> getProductVariations(
            @PathVariable String productId
    ) {
        List<ProductVariationResponseDTO> variations = productVariationService.getAllProductVariations(UUID.fromString(productId));
        return ResponseEntity.ok(variations);
    }

    @Operation(summary = "Update product's variation by ID")
    @PutMapping("/{productId}/variations/{variationId}")
    public ResponseEntity<ProductVariationResponseDTO> updateProductVariation(
            @PathVariable String productId,
            @PathVariable String variationId,
            @Valid @RequestBody UpdateProductVariationBodyDTO requestBody
    ) {
        ProductVariationResponseDTO productVariation = productVariationService.updateProductVariationById(UUID.fromString(variationId), requestBody);
        return ResponseEntity.ok(productVariation);
    }

    @Operation(summary = "Delete product's variation by ID")
    @DeleteMapping("/{productId}/variations/{variationId}")
    public ResponseEntity<Void> deleteProductVariation(
            @PathVariable String productId,
            @PathVariable String variationId
    ) {
        productVariationService.deleteProductVariationById(UUID.fromString(variationId));
        return ResponseEntity.noContent().build();
    }
}