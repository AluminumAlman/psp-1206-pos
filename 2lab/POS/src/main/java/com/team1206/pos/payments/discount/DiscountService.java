package com.team1206.pos.payments.discount;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantRepository;
import com.team1206.pos.user.merchant.MerchantService;
import com.team1206.pos.user.user.User;
import com.team1206.pos.user.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.UUID;

// TODO: add authorization by role and merchant.
@Service
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final UserService userService;

    public DiscountService(DiscountRepository discountRepository,
                           MerchantRepository merchantRepository,
                           UserService userService) {
        this.discountRepository = discountRepository;
        this.userService = userService;
    }

    @Transactional
    public DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO) {
        Merchant merchant = userService.getCurrentUser().getMerchant();
        if(merchant == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first", "");

        Discount discount = setFromRequestDTO(discountRequestDTO, new Discount());
        discount.setMerchant(merchant);
        discountRepository.save(discount);
        return toResponseDTO(discount);
    }

    public Page<DiscountResponseDTO> getDiscounts(int limit, int offset, boolean validOnly) {
        Merchant merchant = userService.getCurrentUser().getMerchant();
        if(merchant == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first", "");

        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Discount> discounts = discountRepository.findAllWithFilters(validOnly, merchant.getId(), pageable);
        return discounts.map(this::toResponseDTO);
    }

    public DiscountResponseDTO getDiscount(UUID id) {
        Discount discount = getDiscountEntity(id);
        userService.verifyLoggedInUserBelongsToMerchant(discount.getMerchant().getId(), "You are not authorized to access this discount");

        return toResponseDTO(discount);
    }

    @Transactional
    public DiscountResponseDTO updateDiscount(UUID id, DiscountRequestDTO discountRequestDTO) {
        Discount discount = getDiscountEntity(id);
        userService.verifyLoggedInUserBelongsToMerchant(discount.getMerchant().getId(), "You are not authorized to update this discount");

        setFromRequestDTO(discountRequestDTO, discount);
        discountRepository.save(discount);
        return toResponseDTO(discount);
    }

    @Transactional
    public void deleteDiscount(UUID id) {
        Discount discount = getDiscountEntity(id);
        userService.verifyLoggedInUserBelongsToMerchant(discount.getMerchant().getId(), "You are not authorized to delete this discount");
        discount.setIsActive(false);
        discountRepository.save(discount);
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
        return response;
    }

    public Discount setFromRequestDTO(DiscountRequestDTO discountRequestDTO, Discount discount)
    {
        discount.setName(discountRequestDTO.getName());
        discount.setPercent(discountRequestDTO.getPercent());
        discount.setAmount(discountRequestDTO.getAmount());
        discount.setValidFrom(discountRequestDTO.getValidFrom());
        discount.setValidUntil(discountRequestDTO.getValidUntil());
        discount.setScope(discountRequestDTO.getScope());
        return discount;
    }

    public Discount getDiscountEntity(UUID discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.DISCOUNT, discountId.toString()));

        if (!discount.getIsActive())
            throw new ResourceNotFoundException(ResourceType.DISCOUNT, discountId.toString());

        return discount;
    }
}
