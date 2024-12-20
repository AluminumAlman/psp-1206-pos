package com.team1206.pos.inventory.product;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create new product")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody CreateProductRequestDTO requestDTO) {
        log.info("Received create product request: {}", requestDTO);

        ProductResponseDTO createdProduct = productService.createProduct(requestDTO);

        log.debug("Returning {} to create product request", createdProduct);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Get all products with filters")
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAllProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        log.info("Received get all products request: name={} price={} categoryId={} offset={} limit={}",
                name, price, categoryId, offset, limit);

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }

        Page<ProductResponseDTO> productPage = productService.getAllProducts(name, price, categoryId, offset, limit);

        log.debug("Returning {} to get all products (name={} price={} categoryId={} offset={} limit={})",
                productPage.toString(), name, price, categoryId, offset, limit);
        return ResponseEntity.ok(productPage);
    }


    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable UUID id) {
        log.info("Received get product request: id={}", id);

        ProductResponseDTO product = productService.getProductById(id);

        log.debug("Returning {} to get product request (id={})", product, id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Update product by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequestDTO requestDTO) {
        log.info("Received update product request: id={} {}", id, requestDTO);

        ProductResponseDTO updatedProduct = productService.updateProductById(id, requestDTO);

        log.debug("Returning {} to update product request (id={})", updatedProduct, id);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete product by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        log.info("Received delete product request: id={}", id);

        productService.deleteProductById(id);

        log.debug("Returning nothing to delete product request (id={})", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adjust product quantity")
    @PatchMapping("/{id}/adjust-quantity")
    public ResponseEntity<ProductResponseDTO> adjustProductQuantity(
            @PathVariable UUID id,
            @Valid @RequestBody AdjustProductQuantityDTO adjustDTO) {
        log.info("Received adjust product quantity request: id={} {}", id, adjustDTO);

        ProductResponseDTO updatedProduct = productService.adjustProductQuantity(id, adjustDTO);

        log.debug("Returning {} to adjust product quantity request (id={})", updatedProduct, id);
        return ResponseEntity.ok(updatedProduct);
    }
}
