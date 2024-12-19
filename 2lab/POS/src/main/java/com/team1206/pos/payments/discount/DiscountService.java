package com.team1206.pos.payments.discount;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

// TODO: add authorization by role and merchant.
@Service
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final UserService userService;

    public DiscountService(DiscountRepository discountRepository,
                           UserService userService) {
        this.discountRepository = discountRepository;
        this.userService = userService;
    }

    @Transactional
    public DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO) {
        if (userService.getCurrentUser().getMerchant() == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first", "");

        Discount discount = mapFromRequestDTO(discountRequestDTO, new Discount());
        discountRepository.save(discount);
        return toResponseDTO(discount);
    }

    @Transactional
    public Page<DiscountResponseDTO> getDiscounts(
            int limit,
            int offset,
            boolean validOnly
    ) {
        LocalDateTime now = LocalDateTime.now();

        Merchant userMerchant = userService.getCurrentUser().getMerchant();
        if (userMerchant.getId() == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first", "");

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Discount> discounts = discountRepository.findAllWithFilters(
                userMerchant.getId(), validOnly, now, pageable);

        return discounts.map(discount -> toResponseDTO(discount));
    }

    public DiscountResponseDTO getDiscount(UUID id) {
        Discount discount = getDiscountEntityById(id);
        return toResponseDTO(discount);
    }

    @Transactional
    public DiscountResponseDTO updateDiscount(UUID id, DiscountRequestDTO discountRequestDTO) {
        Discount discount = getDiscountEntityById(id);
        mapFromRequestDTO(discountRequestDTO, discount);
        discountRepository.save(discount);
        return toResponseDTO(discount);
    }

    @Transactional
    public void deleteDiscount(UUID id) {
        Discount discount = getDiscountEntityById(id);
        discount.setIsActive(false);
        discountRepository.save(discount);
    }

    public Discount getDiscountEntityById(UUID id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString()));
        if (!discount.getIsActive())
            throw new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString());

        return discount;
    }

    public Discount mapFromRequestDTO(DiscountRequestDTO discountRequestDTO, Discount discount) {
        discount.setName(discountRequestDTO.getName());
        discount.setPercent(discountRequestDTO.getPercent());
        discount.setAmount(discountRequestDTO.getAmount());
        discount.setValidFrom(discountRequestDTO.getValidFrom());
        discount.setValidUntil(discountRequestDTO.getValidUntil());
        discount.setScope(discountRequestDTO.getScope());
        return discount;
    }

    public DiscountResponseDTO toResponseDTO(Discount discount) {
        DiscountResponseDTO response = new DiscountResponseDTO();
        response.setId(discount.getId());
        response.setName(discount.getName());
        response.setPercent(discount.getPercent());
        response.setAmount(discount.getAmount());
        response.setValidFrom(discount.getValidFrom());
        response.setValidUntil(discount.getValidUntil());
        response.setMerchantId(discount.getMerchant().getId());
        response.setScope(discount.getScope());
        response.setServiceIds(discount.getServices().stream().map(s -> s.getId()).toList());
        response.setProductCategoryIds(discount.getProductCategories().stream().map(p -> p.getId()).toList());
        response.setProductIds(discount.getProducts().stream().map(p -> p.getId()).toList());
        response.setProductVariationIds(discount.getProductVariations().stream().map(p -> p.getId()).toList());
        response.setCreatedAt(discount.getCreatedAt());
        response.setUpdatedAt(discount.getUpdatedAt());
        return response;
    }
}
