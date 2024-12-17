package com.team1206.pos.inventory.productCategory;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.IllegalStateExceptionWithId;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.MerchantRepository;
import com.team1206.pos.user.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final MerchantRepository merchantRepository; //TODO change to service layer
    private final UserService userService;
    public ProductCategoryService(ProductCategoryRepository productCategoryrepository, MerchantRepository merchantRepository, UserService userService) {
        this.productCategoryRepository = productCategoryrepository;
        this.merchantRepository = merchantRepository;
        this.userService = userService;
    }


    public ProductCategoryResponseDTO createProductCategory(CreateProductCategoryRequestDTO requestDTO) {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();

        Merchant merchant = merchantRepository.findById(requestDTO.getMerchantId()) // TODO pakeisti i getMerchantEntityById is merchantService
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, requestDTO.getMerchantId().toString()));

        if (!merchant.getId().equals(merchantId)) {
            throw new UnauthorizedActionException("You are not authorized to create the category for this merchant", "");
        }

        ProductCategory category = mapToEntity(requestDTO, merchant);
        ProductCategory savedCategory = productCategoryRepository.save(category);
        return mapToResponseDTO(savedCategory);
    }

    public ProductCategoryResponseDTO getProductCategoryById(UUID id) {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();

        if (!merchantId.equals(id)) {
            throw new UnauthorizedActionException("You are not authorized to retrieve this category", "");
        }

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_CATEGORY, id.toString()));
        return mapToResponseDTO(category);
    }

    public List<ProductCategoryResponseDTO> getAllProductCategories() {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();
        return productCategoryRepository.findAllByMerchantId(merchantId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteCategoryById(UUID id) {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_CATEGORY, id.toString()));

        if (!category.getMerchant().getId().equals(merchantId)) {
            throw new UnauthorizedActionException("You are not authorized to delete this category", "");
        }

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateExceptionWithId("Cannot delete category as there are products assigned to it.", id.toString());
        }

        productCategoryRepository.delete(category);
    }

    public ProductCategoryResponseDTO updateCategoryById(UUID id, UpdateProductCategoryRequestDTO requestDTO) {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_CATEGORY, id.toString()));

        if (!category.getMerchant().getId().equals(merchantId)) {
            throw new UnauthorizedActionException("You are not authorized to update this category", "");
        }

        category.setName(requestDTO.getName());

        ProductCategory updatedCategory = productCategoryRepository.save(category);

        return mapToResponseDTO(updatedCategory);
    }


    // Service layer methods
    public ProductCategory getCategoryEntityById(UUID id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_CATEGORY, id.toString()));
    }


    // Mappers
    private ProductCategory mapToEntity(CreateProductCategoryRequestDTO requestDTO, Merchant merchant) {
        ProductCategory category = new ProductCategory();
        category.setName(requestDTO.getName());
        category.setMerchant(merchant);
        return category;
    }

    private ProductCategoryResponseDTO mapToResponseDTO(ProductCategory category) {
        ProductCategoryResponseDTO responseDTO = new ProductCategoryResponseDTO();
        responseDTO.setId(category.getId());
        responseDTO.setName(category.getName());
        responseDTO.setMerchantId(category.getMerchant().getId());
        responseDTO.setCreatedAt(category.getCreatedAt());
        return responseDTO;
    }

}
