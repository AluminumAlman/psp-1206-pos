package com.team1206.pos.user.merchant;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

//TODO issiaiskint ar verta daryt interface
@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    // Create a new merchant
    public MerchantResponseDTO createMerchant(MerchantRequestDTO request) {
        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setPhone(request.getPhone());
        merchant.setEmail(request.getEmail());
        merchant.setCurrency(request.getCurrency());
        merchant.setAddress(request.getAddress());
        merchant.setCity(request.getCity());
        merchant.setCountry(request.getCountry());
        merchant.setPostcode(request.getPostcode());

        Merchant savedMerchant = merchantRepository.save(merchant);
        return mapToResponseDTO(savedMerchant);
    }

    // Get all merchants
    public List<MerchantResponseDTO> getAllMerchants() {
        return merchantRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Map Merchant entity to Response DTO
    private MerchantResponseDTO mapToResponseDTO(Merchant merchant) {
        MerchantResponseDTO response = new MerchantResponseDTO();
        response.setId(merchant.getId());
        response.setName(merchant.getName());
        response.setEmail(merchant.getEmail());
        response.setCurrency(merchant.getCurrency());
        response.setCity(merchant.getCity());
        response.setCountry(merchant.getCountry());
        return response;
    }
}