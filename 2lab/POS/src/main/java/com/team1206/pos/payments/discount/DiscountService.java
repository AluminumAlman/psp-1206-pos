package com.team1206.pos.payments.discount;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.MerchantService;
import com.team1206.pos.user.user.UserService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO: add authorization by role and merchant.
@Service
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final MerchantService merchantService;
    private final UserService userService;

    public DiscountService(DiscountRepository discountRepository,
                           MerchantService merchantService, UserService userService) {
        this.discountRepository = discountRepository;
        this.merchantService = merchantService;
        this.userService = userService;
    }

    @Transactional
    public DiscountResponseDTO createDiscount(CreateDiscountRequestDTO discountRequestDTO) {
        Discount discount = new Discount();

        discount.setName(discountRequestDTO.getName());
        discount.setPercent(discountRequestDTO.getPercent());
        discount.setAmount(discountRequestDTO.getAmount());
        discount.setValidFrom(discountRequestDTO.getValidFrom());
        discount.setValidUntil(discountRequestDTO.getValidUntil());
        discount.setMerchant(merchantService.getMerchantEntityById(userService.getMerchantIdFromLoggedInUser()));

        discountRepository.save(discount);
        return toResponseDTO(discount);
    }

    public List<DiscountResponseDTO> getDiscounts() {
        List<Discount> discounts = discountRepository.findAll();
        List<DiscountResponseDTO> discountResponseDTOS = new ArrayList<>();
        for (Discount discount : discounts) {
            if (discount.getIsActive())
                discountResponseDTOS.add(toResponseDTO(discount));
        }
        return discountResponseDTOS;
    }

    public DiscountResponseDTO getDiscount(UUID id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString()));
        if (!discount.getIsActive())
            throw new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString());

        return toResponseDTO(discount);
    }

    @Transactional
    public DiscountResponseDTO updateDiscount(UUID id, UpdateDiscountRequestDTO discountRequestDTO) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString()));
        if (!discount.getIsActive())
            throw new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString());

        discount.setName(discountRequestDTO.getName());
        discount.setPercent(discountRequestDTO.getPercent());
        discount.setAmount(discountRequestDTO.getAmount());
        discount.setValidFrom(discountRequestDTO.getValidFrom());
        discount.setValidUntil(discountRequestDTO.getValidUntil());

        discountRepository.save(discount);
        return toResponseDTO(discount);
    }

    @Transactional
    public void deleteDiscount(UUID id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString()));
        if (!discount.getIsActive())
            throw new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString());

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
        return response;
    }
}
