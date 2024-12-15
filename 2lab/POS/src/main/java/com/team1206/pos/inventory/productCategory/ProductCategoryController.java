package com.team1206.pos.inventory.productCategory;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/productCategories")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;
    public ProductCategoryController(ProductCategoryService productCategoryService) {this.productCategoryService = productCategoryService;}

    @Operation(summary = "Create product category")
    @PostMapping
    public ResponseEntity<ProductCategoryResponseDTO> createProductCategory(@Valid @RequestBody CreateProductCategoryRequestDTO request) {
        ProductCategoryResponseDTO productCategory = productCategoryService.createProductCategory(request);
        return ResponseEntity.ok(productCategory);
    }

    @Operation(summary = "Get all product categories")
    @GetMapping
    public ResponseEntity<List<ProductCategoryResponseDTO>> getAllProductCategories() {
        List<ProductCategoryResponseDTO> allProductCategories = productCategoryService.getAllProductCategories();
        return ResponseEntity.ok(allProductCategories);
    }

    @Operation(summary = "Get product category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> getProductCategoryById(@PathVariable String id) {
        ProductCategoryResponseDTO category = productCategoryService.getProductCategoryById(UUID.fromString(id));
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Update product category by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> updateCategory(
            @PathVariable String id,
            @RequestBody @Valid UpdateProductCategoryRequestDTO requestDTO) {
        ProductCategoryResponseDTO updatedCategory = productCategoryService.updateCategoryById(UUID.fromString(id), requestDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete product category by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        productCategoryService.deleteCategoryById(UUID.fromString(id));
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }


}
