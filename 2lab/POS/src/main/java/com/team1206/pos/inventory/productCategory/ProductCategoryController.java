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
}
