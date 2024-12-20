package com.team1206.pos.inventory.productCategory;

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
@RequestMapping("/productCategories")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;
    public ProductCategoryController(ProductCategoryService productCategoryService) {this.productCategoryService = productCategoryService;}

    @Operation(summary = "Create product category")
    @PostMapping
    public ResponseEntity<ProductCategoryResponseDTO> createProductCategory(@Valid @RequestBody CreateProductCategoryRequestDTO request) {
        log.info("Received create product category request: {}", request);

        ProductCategoryResponseDTO productCategory = productCategoryService.createProductCategory(request);

        log.debug("Returning {} to create product category request", productCategory);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(productCategory);
    }

    @Operation(summary = "Get all product categories")
    @GetMapping
    public ResponseEntity<List<ProductCategoryResponseDTO>> getAllProductCategories() {
        log.info("Received get all product categories request");

        List<ProductCategoryResponseDTO> allProductCategories = productCategoryService.getAllProductCategories();

        log.debug("Returning {} to get all product categories request", allProductCategories);
        return ResponseEntity.ok(allProductCategories);
    }

    @Operation(summary = "Get product category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> getProductCategoryById(@PathVariable String id) {
        log.info("Received get product category request: id={}", id);

        ProductCategoryResponseDTO category = productCategoryService.getProductCategoryById(UUID.fromString(id));

        log.debug("Returning {} to get product category request (id={})", category, id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Update product category by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductCategoryRequestDTO requestDTO) {
        log.info("Received update product category request: id={}", id);

        ProductCategoryResponseDTO updatedCategory = productCategoryService.updateCategoryById(UUID.fromString(id), requestDTO);

        log.debug("Returning {} to update product category request (id={})", updatedCategory, id);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete product category by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        log.info("Received delete product category request: id={}", id);

        productCategoryService.deleteCategoryById(UUID.fromString(id));

        log.debug("Returning nothing to delete product category request (id={})", id);
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }


}
