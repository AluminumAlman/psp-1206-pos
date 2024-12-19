package com.team1206.pos.inventory.product;

import com.team1206.pos.common.enums.ChargeType;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.IllegalStateExceptionWithId;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productCategory.ProductCategoryService;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.payments.charge.Charge;
import com.team1206.pos.payments.charge.ChargeService;
import com.team1206.pos.user.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryService productCategoryService;
    private final UserService userService;
    private final ChargeService chargeService;

    public ProductService(ProductRepository productRepository, ProductCategoryService productCategoryService, UserService userService, ChargeService chargeService) {
        this.productRepository = productRepository;
        this.productCategoryService = productCategoryService;
        this.userService = userService;
        this.chargeService = chargeService;
    }


    public ProductResponseDTO createProduct(CreateProductRequestDTO requestDTO) {
        ProductCategory category = productCategoryService.getCategoryEntityById(requestDTO.getCategoryId());

        userService.verifyLoggedInUserBelongsToMerchant(category.getMerchant().getId(), "You are not authorized to create products in this category");

        Product product = new Product();
        product.setName(requestDTO.getName());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());
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
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT, id.toString()));

        userService.verifyLoggedInUserBelongsToMerchant(product.getCategory().getMerchant().getId(), "You are not authorized to retrieve this product");
        return mapToResponseDTO(product);
    }

    public Page<ProductResponseDTO> getAllProducts(String name, BigDecimal price, UUID categoryId, int offset, int limit) {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();


        Pageable pageable = PageRequest.of(offset / limit, limit); // Create Pageable object
        Page<Product> productPage;

        if(merchantId == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first");
        else
            productPage = productRepository.findAllWithFilters(merchantId, name, price, categoryId, pageable);

        // Map Page<Product> to Page<ProductResponseDTO>
        return productPage.map(this::mapToResponseDTO);
    }

    public ProductResponseDTO updateProductById(UUID id, UpdateProductRequestDTO updateProductRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT, id.toString()));
        userService.verifyLoggedInUserBelongsToMerchant(product.getCategory().getMerchant().getId(), "You are not authorized to update this product");

        if (updateProductRequestDTO.getName() != null && !updateProductRequestDTO.getName().isBlank()) {
            product.setName(updateProductRequestDTO.getName());
        }

        if (updateProductRequestDTO.getPrice() != null) {
            product.setPrice(updateProductRequestDTO.getPrice());
        }

        if (updateProductRequestDTO.getQuantity() != null) {
            product.setQuantity(updateProductRequestDTO.getQuantity());
        }

        if (updateProductRequestDTO.getCategoryId() != null) {
            ProductCategory category = productCategoryService.getCategoryEntityById(updateProductRequestDTO.getCategoryId());
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
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT, id.toString()));

        userService.verifyLoggedInUserBelongsToMerchant(product.getCategory().getMerchant().getId(), "You are not authorized to delete this product");

        productRepository.deleteById(id);
    }

    public ProductResponseDTO adjustProductQuantity(UUID id, AdjustProductQuantityDTO adjustDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT, id.toString()));

        userService.verifyLoggedInUserBelongsToMerchant(product.getCategory().getMerchant().getId(), "You are not authorized to adjust this product quantity");

        int newQuantity = product.getQuantity() + adjustDTO.getAdjustment();
        if (newQuantity < 0) {
            throw new IllegalStateExceptionWithId("Requested quantity cannot exceed product quantity", id.toString());
        }

        product.setQuantity(newQuantity);

        Product updatedProduct = productRepository.save(product);

        return mapToResponseDTO(updatedProduct);
    }

    // Service layer methods
    public Product getProductEntityById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT, id.toString()));
    }

    // Adjust product quantity
    public void adjustProductQuantity(UUID productId, int adjustment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT, productId.toString()));

        int newQuantity = product.getQuantity() + adjustment;
        if (newQuantity < 0) {
            throw new IllegalStateExceptionWithId("Requested quantity cannot exceed product quantity", productId.toString());
        }

        product.setQuantity(newQuantity);

        productRepository.save(product);
    }

    public BigDecimal getFinalPrice(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT, productId.toString()));

        BigDecimal finalProductPrice = product.getPrice();

        if (product.getCharges() != null) {
            List<Charge> sortedCharges = product.getCharges().stream()
                    .sorted((c1, c2) -> c1.getType() == ChargeType.TAX ? -1 : 1)
                    .toList();

            for (Charge charge : sortedCharges) {
                if (charge.getType() == ChargeType.TAX && charge.getPercent() != null) {
                    BigDecimal multiplier = BigDecimal.valueOf(100 + charge.getPercent())
                            .divide(BigDecimal.valueOf(100));
                    finalProductPrice = finalProductPrice.multiply(multiplier);
                } else if(charge.getType() == ChargeType.SERVICE && charge.getAmount() != null)
                    finalProductPrice = finalProductPrice.add(charge.getAmount());
            }
        }

        finalProductPrice = finalProductPrice.setScale(2, RoundingMode.HALF_UP);


        return finalProductPrice;
    }

    // Helpers
    private List<Charge> validateAndFetchCharges(List<UUID> chargeIds) {
        if (chargeIds == null || chargeIds.isEmpty()) {
            return List.of(); // Return an empty list if no charges are provided
        }

        List<Charge> charges = chargeService.getAllEntitiesById(chargeIds);
        List<UUID> foundChargeIds = charges.stream()
                .map(Charge::getId)
                .toList();

        // Find any missing Charge IDs
        List<UUID> missingChargeIds = chargeIds.stream()
                .filter(id -> !foundChargeIds.contains(id))
                .toList();

        if (!missingChargeIds.isEmpty()) {
            throw new ResourceNotFoundException(ResourceType.CHARGE, missingChargeIds.toString()); // TODO patestuoti kai bus Charges
        }

        return charges;
    }


    // Mappers
    private ProductResponseDTO mapToResponseDTO(Product product) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setId(product.getId());
        responseDTO.setName(product.getName());
        responseDTO.setPrice(product.getPrice());
        responseDTO.setQuantity(product.getQuantity());
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
        responseDTO.setUpdatedAt(product.getUpdatedAt());
        return responseDTO;
    }
}