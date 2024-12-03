package com.team1206.pos.inventory.product;

import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.inventory.productCategory.ProductCategoryRepository;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.inventory.productVariation.ProductVariationRepository;
import com.team1206.pos.payments.charge.ChargeRepository;
import com.team1206.pos.payments.charge.Charge;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductVariationRepository productVariationRepository;
    private final ChargeRepository chargeRepository;
    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ProductVariationRepository productVariationRepository, ChargeRepository chargeRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productVariationRepository = productVariationRepository;
        this.chargeRepository = chargeRepository;
    }


    // Create new Product
    public ProductResponseDTO createProduct(createProductRequestDTO requestDTO) {
        Product product = new Product();

        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());

        ProductCategory category = productCategoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for ID: " + requestDTO.getCategoryId()));
        product.setCategory(category);

        // Map variations if provided
        if (requestDTO.getVariationIds() != null && !requestDTO.getVariationIds().isEmpty()) {
            List<ProductVariation> variations = productVariationRepository.findAllById(requestDTO.getVariationIds());
            product.setVariations(variations);
        }

        // Map charges if provided (optional)
        if (requestDTO.getChargeIds() != null && !requestDTO.getChargeIds().isEmpty()) {
            List<Charge> charges = chargeRepository.findAllById(requestDTO.getChargeIds());
            product.setCharges(charges);
        }

        Product savedProduct = productRepository.save(product);

        return mapToResponseDTO(savedProduct);
    }

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
