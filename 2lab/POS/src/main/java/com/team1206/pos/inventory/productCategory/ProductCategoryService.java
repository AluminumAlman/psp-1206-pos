package com.team1206.pos.inventory.productCategory;

import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final MerchantRepository merchantRepository;
    public ProductCategoryService(ProductCategoryRepository productCategoryrepository, MerchantRepository merchantRepository) {
        this.productCategoryRepository = productCategoryrepository;
        this.merchantRepository = merchantRepository;
    }


    public ProductCategoryResponseDTO createProductCategory(CreateProductCategoryRequestDTO requestDTO) {
        Merchant merchant = merchantRepository.findById(requestDTO.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found for ID: " + requestDTO.getMerchantId())); // TO-DO exceptionHandler

        ProductCategory category = mapToEntity(requestDTO, merchant);
        ProductCategory savedCategory = productCategoryRepository.save(category);
        return mapToResponseDTO(savedCategory);
    }

    public ProductCategoryResponseDTO getProductCategory(UUID id) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory not found for ID: " + id)); // TO-DO exceptionHandler
        return mapToResponseDTO(category);
    }

    public List<ProductCategoryResponseDTO> getAllProductCategories() {
        return productCategoryRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteCategory(UUID id) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id)); // TO-DO exceptionHandler

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category as there are products assigned to it."); // TO-DO exceptionHandler
        }

        productCategoryRepository.delete(category);
    }

    public ProductCategoryResponseDTO updateCategory(UUID id, UpdateProductCategoryRequestDTO requestDTO) {

        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for ID: " + id)); // TO-DO exceptionHandler

        category.setName(requestDTO.getName());

        ProductCategory updatedCategory = productCategoryRepository.save(category);

        return mapToResponseDTO(updatedCategory);
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
