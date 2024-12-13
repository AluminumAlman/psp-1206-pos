package com.team1206.pos.payments.discount;

import com.team1206.pos.authentication.security.JWTUtil;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantRepository;
import com.team1206.pos.user.user.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO: add authorization by role and merchant.
@Service
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final JWTUtil jwtUtil;

    public DiscountService(DiscountRepository discountRepository,
                           UserRepository userRepository,
                           MerchantRepository merchantRepository,
                           JWTUtil jwtUtil) {
        this.discountRepository = discountRepository;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.jwtUtil = jwtUtil;
    }

    public DiscountResponseDTO createDiscount(DiscountRequestDTO discountRequestDTO) {
        Discount discount = mapRequestDTOToEntity(discountRequestDTO, new Discount());
        discountRepository.save(discount);
        return toResponseDTO(discount);
    }

    public List<DiscountResponseDTO> getDiscounts() {
        List<Discount> discounts = discountRepository.findAll();
        List<DiscountResponseDTO> discountResponseDTOS = new ArrayList<>();
        for (Discount discount : discounts) {
            discountResponseDTOS.add(toResponseDTO(discount));
        }
        return discountResponseDTOS;
    }

    public DiscountResponseDTO getDiscount(UUID id) throws ResourceNotFoundException {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString()));
        return toResponseDTO(discount);
    }

    @Transactional
    public DiscountResponseDTO updateDiscount(UUID id, DiscountRequestDTO discountRequestDTO) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString()));
        mapRequestDTOToEntity(discountRequestDTO, discount);
        discountRepository.save(discount);
        return toResponseDTO(discount);
    }

    @Transactional
    public void deleteDiscount(UUID id) {
        if (!discountRepository.existsById(id)) {
            throw new ResourceNotFoundException(ResourceType.DISCOUNT, id.toString());
        }
        try {
            discountRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while deleting discount with ID: " + id, e);
        }
    }

    public Discount mapRequestDTOToEntity(DiscountRequestDTO discountRequestDTO, Discount discount) {
        discount.setName(discountRequestDTO.getName());
        discount.setPercent(discountRequestDTO.getPercent());
        discount.setAmount(discountRequestDTO.getAmount());
        discount.setValidFrom(discountRequestDTO.getValidFrom());
        discount.setValidUntil(discountRequestDTO.getValidUntil());
        UUID merchantId = discountRequestDTO.getMerchantId();
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, merchantId.toString()));
        discount.setMerchant(merchant);
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
        return response;
    }
}
