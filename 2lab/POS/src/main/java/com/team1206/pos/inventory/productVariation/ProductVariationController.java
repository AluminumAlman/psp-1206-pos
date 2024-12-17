package com.team1206.pos.inventory.productVariation;

import com.team1206.pos.inventory.product.AdjustProductQuantityDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
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
        log.info("Received create product variation request: productId={} {}", productId, requestBody);

        ProductVariationResponseDTO createdVariation =
                productVariationService.createProductVariation(UUID.fromString(productId), requestBody);

        log.debug("Returning {} to create product variation request (productId={})", createdVariation, productId);
        // TODO: add to response the URI to created product variation.
        return ResponseEntity.ok(createdVariation);
    }

    @Operation(summary = "Get product's variation by ID")
    @GetMapping("/{productId}/variations/{variationId}")
    public ResponseEntity<ProductVariationResponseDTO> getProductVariation(
            @PathVariable String productId,
            @PathVariable String variationId
    ) {
        log.info("Received get product variation request: productId={} variationId={}", productId, variationId);

        // TODO Ar verta tikrinti, ar variacija atitinka productId is url ir ar tas produktas egzistuoja
        ProductVariationResponseDTO variation = productVariationService.getProductVariationById(UUID.fromString(variationId));

        log.debug("Returning {} to get product variation request (productId={} variationId={})", variation, productId, variationId);
        return ResponseEntity.ok(variation);
    }

    @Operation(summary = "Get all product's variations")
    @GetMapping("/{productId}/variations")
    public ResponseEntity<List<ProductVariationResponseDTO>> getProductVariations(
            @PathVariable String productId
    ) {
        log.info("Received get all product variations request (productId={})", productId);

        List<ProductVariationResponseDTO> variations = productVariationService.getAllProductVariations(UUID.fromString(productId));

        log.debug("Returning {} to get all product variations (productId={})", variations, productId);
        return ResponseEntity.ok(variations);
    }

    @Operation(summary = "Update product's variation by ID")
    @PutMapping("/{productId}/variations/{variationId}")
    public ResponseEntity<ProductVariationResponseDTO> updateProductVariation(
            @PathVariable String productId,
            @PathVariable String variationId,
            @Valid @RequestBody UpdateProductVariationBodyDTO requestBody
    ) {
        log.info("Received update product variation request: productId={} variationId={} {}", productId, variationId, requestBody);

        ProductVariationResponseDTO productVariation = productVariationService.updateProductVariationById(UUID.fromString(variationId), requestBody);

        log.debug("Returning {} to update product variation request (productId={} variationId={})", productVariation, productId, variationId);
        return ResponseEntity.ok(productVariation);
    }

    @Operation(summary = "Delete product's variation by ID")
    @DeleteMapping("/{productId}/variations/{variationId}")
    public ResponseEntity<Void> deleteProductVariation(
            @PathVariable String productId,
            @PathVariable String variationId
    ) {
        log.info("Received delete product variation request: productId={} variationId={}", productId, variationId);

        productVariationService.deleteProductVariationById(UUID.fromString(variationId));

        log.debug("Returning nothing to delete product variation request (productId={} variationId={})", productId, variationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adjust product variation quantity")
    @PatchMapping("/{productId}/variations/{variationId}/adjust-quantity")
    public ResponseEntity<ProductVariationResponseDTO> adjustProductVariationQuantity(
            @PathVariable UUID productId,
            @PathVariable UUID variationId,
            @Valid @RequestBody AdjustProductQuantityDTO adjustDTO) {
        ProductVariationResponseDTO updatedProductVariation = productVariationService.adjustProductVariationQuantity(variationId, adjustDTO);
        return ResponseEntity.ok(updatedProductVariation);
    }
}
