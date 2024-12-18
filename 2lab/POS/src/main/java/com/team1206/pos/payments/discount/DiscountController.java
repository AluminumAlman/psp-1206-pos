package com.team1206.pos.payments.discount;

import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.product.ProductResponseDTO;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productCategory.ProductCategoryResponseDTO;
import com.team1206.pos.inventory.productVariation.ProductVariationResponseDTO;
import com.team1206.pos.service.service.Service;
import com.team1206.pos.service.service.ServiceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<DiscountResponseDTO>> getDiscounts(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "validOnly", defaultValue = "true") boolean validOnly) {
        log.info("Received get discounts request: limit={} offset={} validOnly={}", limit, offset, validOnly);

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }

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


    //@GetMapping("{discountId}/services")
    @Operation(summary = "Get services to which a discount is applied")
    public ResponseEntity<Page<ServiceResponseDTO>> getDiscountServices(@PathVariable("discountId") UUID discountId) {
        log.info("Received get discount services request: discountId={}", discountId);

        log.debug("Returning {} to get discount services request (discountId={})", discountId);
        return null;
    }

    //@PostMapping("{discountId}/services")
    @Operation(summary = "Add services to which a discount should be applied")
    public ResponseEntity<List<ServiceResponseDTO>> postDiscountServices(@PathVariable("discountId") UUID discountId,
                                                                         @RequestBody List<UUID> serviceIds) {
        log.info("Received add discount services request: discountId={} {}", discountId, serviceIds);

        log.debug("Returning {} to add discount services request (discountId={})", discountId);
        return null;
    }

    //@DeleteMapping("{discountId}/services")
    @Operation(summary = "Remove services to which a discount should be applied")
    public ResponseEntity<Void> deleteDiscountServices(@PathVariable("discountId") UUID discountId,
                                                       @RequestBody List<UUID> serviceIds) {
        log.info("Received remove discount services request: discountId={} {}", discountId, serviceIds);

        log.debug("Returning nothing to remove discount services request (discountId={})", discountId);
        return ResponseEntity.noContent().build();
    }


    //@GetMapping("{discountId}/productCategories")
    @Operation(summary = "Get product categories to which a discount is applied")
    public ResponseEntity<Page<ProductCategoryResponseDTO>> getDiscountProductCategories(@PathVariable("discountId") UUID discountId) {
        log.info("Received get discount product categories request: discountId={}", discountId);

        log.debug("Returning {} to get discount product categories request (discountId={})", discountId);
        return null;
    }

    //@PostMapping("{discountId}/productCategories")
    @Operation(summary = "Add product categories to which a discount should be applied")
    public ResponseEntity<List<ProductCategoryResponseDTO>> postDiscountProductCategories(@PathVariable("discountId") UUID discountId,
                                                                                          @RequestBody List<UUID> productCategoryIds) {
        log.info("Received add discount product categories request: discountId={} {}", discountId, productCategoryIds);

        log.debug("Returning {} to add discount product categories request (discountId={})", discountId);
        return null;
    }

    //@DeleteMapping("{discountId}/productCategories")
    @Operation(summary = "Remove product categories to which a discount should be applied")
    public ResponseEntity<Void> deleteDiscountProductCategories(@PathVariable("discountId") UUID discountId,
                                                                @RequestBody List<UUID> productCategoryIds) {
        log.info("Received remove discount product categories request: discountId={} {}", discountId, productCategoryIds);

        log.debug("Returning nothing to remove discount product categories request (discountId={})", discountId);
        return ResponseEntity.noContent().build();
    }


    //@GetMapping("{discountId}/products")
    @Operation(summary = "Get products to which a discount is applied")
    public ResponseEntity<Page<ProductResponseDTO>> getDiscountProducts(@PathVariable("discountId") UUID discountId) {
        log.info("Received get discount products request: discountId={}", discountId);

        log.debug("Returning {} to get discount products request (discountId={})", discountId);
        return null;
    }

    //@PostMapping("{discountId}/products")
    @Operation(summary = "Add products to which a discount should be applied")
    public ResponseEntity<List<ProductResponseDTO>> postDiscountProducts(@PathVariable("discountId") UUID discountId,
                                                                         @RequestBody List<UUID> productIds) {
        log.info("Received add discount products request: discountId={} {}", discountId, productIds);

        log.debug("Returning {} to add discount products request (discountId={})", discountId);
        return null;
    }

    //@DeleteMapping("{discountId}/products")
    @Operation(summary = "Remove products to which a discount should be applied")
    public ResponseEntity<Void> deleteDiscountProducts(@PathVariable("discountId") UUID discountId,
                                                       @RequestBody List<UUID> productIds) {
        log.info("Received remove discount products request: discountId={} {}", discountId, productIds);

        log.debug("Returning nothing to remove discount products request (discountId={})", discountId);
        return ResponseEntity.noContent().build();
    }


    //@GetMapping("{discountId}/productVariations")
    @Operation(summary = "Get product variations to which a discount is applied")
    public ResponseEntity<Page<ProductVariationResponseDTO>> getDiscountProductVariations(@PathVariable("discountId") UUID discountId) {
        log.info("Received get discount product variations request: discountId={}", discountId);

        log.debug("Returning {} to get discount product variations request (discountId={})", discountId);
        return null;
    }

    //@PostMapping("{discountId}/productVariations")
    @Operation(summary = "Add product variations to which a discount should be applied")
    public ResponseEntity<List<ProductVariationResponseDTO>> postDiscountProductVariations(@PathVariable("discountId") UUID discountId,
                                                                                           @RequestBody List<UUID> productVariationIds) {
        log.info("Received add discount product variations request: discountId={} {}", discountId, productVariationIds);

        log.debug("Returning {} to add discount product variations request (discountId={})", discountId);
        return null;
    }

    //@DeleteMapping("{discountId}/productVariations")
    @Operation(summary = "Remove product variations to which a discount should be applied")
    public ResponseEntity<Void> deleteDiscountProductVariations(@PathVariable("discountId") UUID discountId,
                                                                @RequestBody List<UUID> productVariationIds) {
        log.info("Received remove discount product variations request: discountId={} {}", discountId, productVariationIds);

        log.debug("Returning nothing to remove discount product variations request (discountId={})", discountId);
        return ResponseEntity.noContent().build();
    }
}
