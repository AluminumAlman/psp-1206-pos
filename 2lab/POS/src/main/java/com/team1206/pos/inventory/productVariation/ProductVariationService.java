package com.team1206.pos.inventory.productVariation;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.product.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductVariationService {
    private final ProductVariationRepository productVariationRepository;
    private final ProductService productService;

    public ProductVariationService(ProductVariationRepository productVariationRepository, ProductService productService) {
        this.productVariationRepository = productVariationRepository;
        this.productService = productService;
    }

    public ProductVariationResponseDTO createProductVariation(UUID productId, CreateProductVariationBodyDTO productVariationDTO) {
        Product product = productService.getProductEntityById(productId);

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
        productService.getProductEntityById(productId); // To check whether exists

        List<ProductVariation> productVariations = productVariationRepository.findByProduct_Id(productId);

        return productVariations.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public ProductVariationResponseDTO updateProductVariationById (UUID productVariationId, UpdateProductVariationBodyDTO updateProductVariationBodyDTO)
    {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, productVariationId.toString()));

        if(updateProductVariationBodyDTO.getName() != null && !updateProductVariationBodyDTO.getName().isBlank())
            productVariation.setName(updateProductVariationBodyDTO.getName());
        if(updateProductVariationBodyDTO.getPrice() != null)
            productVariation.setPrice(updateProductVariationBodyDTO.getPrice());

        productVariationRepository.save(productVariation);
        return mapToResponseDTO(productVariation);
    }

    public void deleteProductVariationById(UUID productVariationId)
    {
        if(!productVariationRepository.existsById(productVariationId))
            throw new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, productVariationId.toString());
        productVariationRepository.deleteById(productVariationId);
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

