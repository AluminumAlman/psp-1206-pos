package com.team1206.pos.inventory.productVariation;

import com.team1206.pos.common.enums.ChargeType;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.IllegalStateExceptionWithId;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.inventory.inventoryLog.InventoryLogService;
import com.team1206.pos.inventory.product.AdjustProductQuantityDTO;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.product.ProductService;
import com.team1206.pos.payments.charge.Charge;
import com.team1206.pos.user.user.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductVariationService {
    private final ProductVariationRepository productVariationRepository;
    private final ProductService productService;
    private final UserService userService;
    private final InventoryLogService inventoryLogService;

    public ProductVariationService(ProductVariationRepository productVariationRepository, ProductService productService, UserService userService, @Lazy InventoryLogService inventoryLogService) {
        this.productVariationRepository = productVariationRepository;
        this.productService = productService;
        this.userService = userService;
        this.inventoryLogService = inventoryLogService;
    }

    public ProductVariationResponseDTO createProductVariation(UUID productId, CreateProductVariationBodyDTO productVariationDTO) {
        Product product = productService.getProductEntityById(productId);

        userService.verifyLoggedInUserBelongsToMerchant(product.getCategory().getMerchant().getId(), "You are not authorized to create product variation for this product");

        ProductVariation productVariation = new ProductVariation();
        productVariation.setName(productVariationDTO.getName());
        productVariation.setPrice(productVariationDTO.getPrice());
        productVariation.setQuantity(productVariationDTO.getQuantity());
        productVariation.setProduct(product);
        productVariation.setCreatedAt(LocalDateTime.now());

        productVariationRepository.save(productVariation);
        return mapToResponseDTO(productVariation);
    }

    public ProductVariationResponseDTO getProductVariationById (UUID productId, UUID productVariationId)
    {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, productVariationId.toString()));

        if(!productVariation.getProduct().getId().equals(productId))
            throw new IllegalArgumentException("Variation doesn't match the product");

        userService.verifyLoggedInUserBelongsToMerchant(productVariation.getProduct().getCategory().getMerchant().getId(), "You are not authorized to retrieve this product variation");

        return mapToResponseDTO(productVariation);
    }

    public List<ProductVariationResponseDTO> getAllProductVariations(UUID productId) {
        productService.getProductEntityById(productId); // To check whether exists
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();

        List<ProductVariation> productVariations;
        if(merchantId == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first");
        else
            productVariations = productVariationRepository.findAllWithFilters(productId, merchantId);

        return productVariations.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public ProductVariationResponseDTO updateProductVariationById (UUID productId, UUID productVariationId, UpdateProductVariationBodyDTO updateProductVariationBodyDTO)
    {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, productVariationId.toString()));

        if(!productVariation.getProduct().getId().equals(productId))
            throw new IllegalArgumentException("Variation doesn't match the product");

        userService.verifyLoggedInUserBelongsToMerchant(productVariation.getProduct().getCategory().getMerchant().getId(), "You are not authorized to update this product variation");

        if(updateProductVariationBodyDTO.getName() != null && !updateProductVariationBodyDTO.getName().isBlank())
            productVariation.setName(updateProductVariationBodyDTO.getName());
        if(updateProductVariationBodyDTO.getPrice() != null)
            productVariation.setPrice(updateProductVariationBodyDTO.getPrice());
        if(updateProductVariationBodyDTO.getQuantity() != null && !updateProductVariationBodyDTO.getQuantity().equals(productVariation.getQuantity())) {
            productVariation.setQuantity(updateProductVariationBodyDTO.getQuantity());
            inventoryLogService.createInventoryLogForProductVariation(productVariationId, updateProductVariationBodyDTO.getQuantity(), null);
        }
        productVariationRepository.save(productVariation);
        return mapToResponseDTO(productVariation);
    }

    public void deleteProductVariationById(UUID productId, UUID productVariationId)
    {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, productVariationId.toString()));

        if(!productVariation.getProduct().getId().equals(productId))
            throw new IllegalArgumentException("Variation doesn't match the product");

        userService.verifyLoggedInUserBelongsToMerchant(productVariation.getProduct().getCategory().getMerchant().getId(), "You are not authorized to delete this product variation");

        productVariationRepository.deleteById(productVariationId);
    }

    public ProductVariationResponseDTO adjustProductVariationQuantity(UUID productId, UUID variationId, AdjustProductQuantityDTO adjustDTO) {
        ProductVariation productVariation = productVariationRepository.findById(variationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT, variationId.toString()));

        if(!productVariation.getProduct().getId().equals(productId))
            throw new IllegalArgumentException("Variation doesn't match the product");

        userService.verifyLoggedInUserBelongsToMerchant(productVariation.getProduct().getCategory().getMerchant().getId(), "You are not authorized to adjust this product variation quantity");

        int newQuantity = productVariation.getQuantity() + adjustDTO.getAdjustment();
        if (newQuantity < 0) {
            throw new IllegalStateExceptionWithId("Requested quantity cannot exceed product variation quantity", variationId.toString());
        }

        productVariation.setQuantity(newQuantity);

        ProductVariation updatedProductVariation = productVariationRepository.save(productVariation);

        inventoryLogService.createInventoryLogForProductVariation(variationId, productVariation.getQuantity(), null);

        return mapToResponseDTO(updatedProductVariation);
    }

    // Service layer

    public ProductVariation getProductVariationEntityById(UUID variationId) {
        return productVariationRepository.findById(variationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, variationId.toString()));


    }

    // Adjust product variation quantity
    public void adjustProductVariationQuantity(UUID productVariationId, int adjustment) {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, productVariationId.toString()));

        userService.verifyLoggedInUserBelongsToMerchant(productVariation.getProduct().getCategory().getMerchant().getId(), "You are not authorized to adjust this product variation quantity");

        int newQuantity = productVariation.getQuantity() + adjustment;
        if (newQuantity < 0) {
            throw new IllegalStateExceptionWithId("Requested quantity cannot exceed product variation quantity", productVariationId.toString());
        }

        productVariation.setQuantity(newQuantity);

        productVariationRepository.save(productVariation);
    }

    public BigDecimal getFinalPrice(UUID productVariationId) {
        ProductVariation productVariation = productVariationRepository.findById(productVariationId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_VARIATION, productVariationId.toString()));

        BigDecimal finalProductVariationPrice = productVariation.getPrice();

        Product product = productVariation.getProduct();

        if (product.getCharges() != null) {
            List<Charge> sortedCharges = product.getCharges().stream()
                    .sorted((c1, c2) -> c1.getType() == ChargeType.TAX ? -1 : 1)
                    .toList();

            for (Charge charge : sortedCharges) {
                if (charge.getType() == ChargeType.TAX && charge.getPercent() != null) {
                    BigDecimal multiplier = BigDecimal.valueOf(100 + charge.getPercent())
                            .divide(BigDecimal.valueOf(100));
                    finalProductVariationPrice = finalProductVariationPrice.multiply(multiplier);
                } else if(charge.getType() == ChargeType.SERVICE && charge.getAmount() != null)
                    finalProductVariationPrice = finalProductVariationPrice.add(charge.getAmount());
            }
        }

        finalProductVariationPrice = finalProductVariationPrice.setScale(2, RoundingMode.HALF_UP);


        return finalProductVariationPrice;
    }

    // Mappers
    private ProductVariationResponseDTO mapToResponseDTO(ProductVariation productVariation) {
        ProductVariationResponseDTO responseDTO = new ProductVariationResponseDTO();
        responseDTO.setId(productVariation.getId());
        responseDTO.setName(productVariation.getName());
        responseDTO.setPrice(productVariation.getPrice());
        responseDTO.setQuantity(productVariation.getQuantity());
        responseDTO.setCreatedAt(productVariation.getCreatedAt());
        responseDTO.setUpdatedAt(productVariation.getUpdatedAt());
        return responseDTO;
    }
}

