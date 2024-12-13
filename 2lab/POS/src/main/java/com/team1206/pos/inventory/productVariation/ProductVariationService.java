package com.team1206.pos.inventory.productVariation;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.product.ProductRepository;
import com.team1206.pos.inventory.product.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductVariationService {
    private final ProductVariationRepository productVariationRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductVariationService(ProductVariationRepository productVariationRepository, ProductService productService, ProductRepository productRepository) {
        this.productVariationRepository = productVariationRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    public ProductVariationResponseDTO createProductVariation(CreateProductVariationRequestDTO productVariationDTO) {
        Product product = productService.getProductEntityById(productVariationDTO.getProductId());

        ProductVariation productVariation = new ProductVariation();
        productVariation.setName(productVariationDTO.getName());
        productVariation.setPrice(productVariationDTO.getPrice());
        productVariation.setProduct(product);
        productVariation.setCreatedAt(LocalDateTime.now());

        productVariationRepository.save(productVariation);
        return mapToResponseDTO(productVariation);
    }

    public ProductVariationResponseDTO getProductVariationById (UUID productVariationId)
    {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, productVariationId.toString()));


        return mapToResponseDTO(productVariation);
    }

    public List<ProductVariationResponseDTO> getAllProductVariations(UUID productId) {
        List<ProductVariation> productVariations = productVariationRepository.findByProduct_Id(productId);

        return productVariations.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Mappers
    private ProductVariationResponseDTO mapToResponseDTO(ProductVariation productVariation) {
        ProductVariationResponseDTO responseDTO = new ProductVariationResponseDTO();
        responseDTO.setId(productVariation.getId());
        responseDTO.setName(productVariation.getName());
        responseDTO.setPrice(productVariation.getPrice());
        responseDTO.setCreatedAt(productVariation.getCreatedAt());
        return responseDTO;
    }
}

