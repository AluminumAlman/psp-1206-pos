package com.team1206.pos.inventory.productCategory;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.IllegalStateExceptionWithId;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.MerchantService;
import com.team1206.pos.user.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final UserService userService;
    private final MerchantService merchantService;

    public ProductCategoryService(ProductCategoryRepository productCategoryrepository, UserService userService, MerchantService merchantService) {
        this.productCategoryRepository = productCategoryrepository;
        this.userService = userService;
        this.merchantService = merchantService;
    }


    public ProductCategoryResponseDTO createProductCategory(CreateProductCategoryRequestDTO requestDTO) {

        UUID merchantId = userService.getMerchantIdFromLoggedInUser();
        if (merchantId == null) {
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first");
        }

        ProductCategory category = mapToEntity(requestDTO, merchantService.getMerchantEntityById(merchantId));
        ProductCategory savedCategory = productCategoryRepository.save(category);
        return mapToResponseDTO(savedCategory);
    }

    public ProductCategoryResponseDTO getProductCategoryById(UUID id) {
        userService.verifyLoggedInUserBelongsToMerchant(id, "You are not authorized to retrieve this category");


        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_CATEGORY, id.toString()));
        return mapToResponseDTO(category);
    }

    public List<ProductCategoryResponseDTO> getAllProductCategories() {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();

        List<ProductCategory> productCategories;
        if(merchantId == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first");
        else
            productCategories = productCategoryRepository.findAllByMerchantId(merchantId);


        return productCategories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteCategoryById(UUID id) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_CATEGORY, id.toString()));

        userService.verifyLoggedInUserBelongsToMerchant(category.getMerchant().getId(), "You are not authorized to delete this category");

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateExceptionWithId("Cannot delete category as there are products assigned to it.", id.toString());
        }

        productCategoryRepository.delete(category);
    }

    public ProductCategoryResponseDTO updateCategoryById(UUID id, UpdateProductCategoryRequestDTO requestDTO) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PRODUCT_CATEGORY, id.toString()));

        userService.verifyLoggedInUserBelongsToMerchant(category.getMerchant().getId(), "You are not authorized to update this category");


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
