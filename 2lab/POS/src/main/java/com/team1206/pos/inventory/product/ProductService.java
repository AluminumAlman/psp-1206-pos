package com.team1206.pos.inventory.product;

import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.inventory.productCategory.ProductCategoryRepository;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.payments.charge.ChargeRepository;
import com.team1206.pos.payments.charge.Charge;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

// TO-DO change Integer to floating after model is changed
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ChargeRepository chargeRepository;

    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ChargeRepository chargeRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.chargeRepository = chargeRepository;
    }


    public ProductResponseDTO createProduct(CreateProductRequestDTO requestDTO) {
        ProductCategory category = productCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for ID: " + requestDTO.getCategoryId())); // TO-DO ExceptionHandler
        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());
        product.setCategory(category);

        // Check for missing ChargeIds
        if (!requestDTO.getChargeIds().isEmpty()) {
            List<Charge> charges = validateAndFetchCharges(requestDTO.getChargeIds());
            product.setCharges(charges);
        }

        Product savedProduct = productRepository.save(product);

        return mapToResponseDTO(savedProduct);
    }

    public ProductResponseDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id)); // TO-DO ExceptionHandler
        return mapToResponseDTO(product);
    }

    public Page<ProductResponseDTO> getAllProducts(String name, Integer price, UUID categoryId, int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit); // Create Pageable object
        Page<Product> productPage = productRepository.findAllWithFilters(name, price, categoryId, pageable);

        // Map Page<Product> to Page<ProductResponseDTO>
        return productPage.map(this::mapToResponseDTO);
    }

    public ProductResponseDTO updateProduct(UUID id, UpdateProductRequestDTO updateProductRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id)); // TO-DO exceptionHandler

        if (updateProductRequestDTO.getName() != null && !updateProductRequestDTO.getName().isBlank()) {
            product.setName(updateProductRequestDTO.getName());
        }

        if (updateProductRequestDTO.getPrice() != null) {
            product.setPrice(updateProductRequestDTO.getPrice());
        }

        if (updateProductRequestDTO.getCategoryId() != null) {
            ProductCategory category = productCategoryRepository.findById(updateProductRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found for ID: " + updateProductRequestDTO.getCategoryId()));  // TO-DO exceptionHandler
            product.setCategory(category);
        }

        if (updateProductRequestDTO.getChargeIds() != null) {
            List<Charge> charges = validateAndFetchCharges(updateProductRequestDTO.getChargeIds());
            product.setCharges(charges);
        }

        Product updatedProduct = productRepository.save(product);

        return mapToResponseDTO(updatedProduct);
    }

    public void deleteProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id)); // TO-DO ExceptionHandler

        productRepository.delete(product);
    }

    // Helpers
    private List<Charge> validateAndFetchCharges(List<UUID> chargeIds) {
        if (chargeIds == null || chargeIds.isEmpty()) {
            return List.of(); // Return an empty list if no charges are provided
        }

        List<Charge> charges = chargeRepository.findAllById(chargeIds);
        List<UUID> foundChargeIds = charges.stream()
                .map(Charge::getId)
                .toList();

        // Find any missing Charge IDs
        List<UUID> missingChargeIds = chargeIds.stream()
                .filter(id -> !foundChargeIds.contains(id))
                .toList();

        if (!missingChargeIds.isEmpty()) {
            throw new ResourceNotFoundException("Some charges were not found for IDs: " + missingChargeIds);
        }

        return charges;
    }


    // Mappers
    private ProductResponseDTO mapToResponseDTO(Product product) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(product.getId());
        responseDTO.setName(product.getName());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setCategoryId(product.getCategory().getId());

        if (product.getVariations() != null) {
            responseDTO.setVariationIds(product.getVariations().stream()
                    .map(ProductVariation::getId)
                    .toList());
        }

        if (product.getCharges() != null) {
            responseDTO.setChargeIds(product.getCharges().stream()
                    .map(Charge::getId)
                    .toList());
        }

        responseDTO.setCreatedAt(product.getCreatedAt());
        return responseDTO;
    }
}