package com.team1206.pos.inventory.productCategory;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productCategories")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;
    public ProductCategoryController(ProductCategoryService productCategoryService) {this.productCategoryService = productCategoryService;}

    @PostMapping
    public ResponseEntity<ProductCategoryResponseDTO> createProductCategory(@Valid @RequestBody CreateProductCategoryRequestDTO request) {
        ProductCategoryResponseDTO productCategory = productCategoryService.createProductCategory(request);
        return ResponseEntity.ok(productCategory);
    }

    @GetMapping
    public ResponseEntity<List<ProductCategoryResponseDTO>> getAllProductCategories() {
        List<ProductCategoryResponseDTO> allProductCategories = productCategoryService.getAllProductCategories();
        return ResponseEntity.ok(allProductCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> getProductCategoryById(@PathVariable String id) {
        ProductCategoryResponseDTO category = productCategoryService.getProductCategory(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> updateCategory(
            @PathVariable String id,
            @RequestBody @Valid UpdateProductCategoryRequestDTO requestDTO) {
        ProductCategoryResponseDTO updatedCategory = productCategoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        productCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }


}
