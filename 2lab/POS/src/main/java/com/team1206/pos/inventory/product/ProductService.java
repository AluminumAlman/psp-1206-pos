package com.team1206.pos.inventory.product;

import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.inventory.productCategory.ProductCategoryRepository;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.payments.charge.ChargeRepository;
import com.team1206.pos.payments.charge.Charge;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

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
            List<Charge> charges = chargeRepository.findAllById(requestDTO.getChargeIds());
            List<UUID> foundChargeIds = charges.stream()
                    .map(Charge::getId)
                    .toList();

            // Find missing charges
            List<UUID> missingChargeIds = requestDTO.getChargeIds().stream()
                    .filter(id -> !foundChargeIds.contains(id))
                    .toList();

            if (!missingChargeIds.isEmpty()) {
                throw new IllegalArgumentException("Some charges were not found: " + missingChargeIds);
            }

            product.setCharges(charges);
        }

        Product savedProduct = productRepository.save(product);

        return mapToResponseDTO(savedProduct);
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
