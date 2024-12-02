package com.team1206.pos.user.merchant;

import com.team1206.pos.exceptions.MerchantNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

//TODO issiaiskint ar verta daryt interface
// Man atrodo turi buti klase (viskas yra gerai)
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

    // Retrieve merchant by ID
    public MerchantResponseDTO getMerchantById(UUID id) {
        return merchantRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new MerchantNotFoundException(id.toString()));
    }

    // Delete merchant by ID
    public void deleteMerchantById(UUID id) {
        Optional<Merchant> merchant = merchantRepository.findById(id);
        if (merchant.isPresent()) {
            try {
                merchantRepository.deleteById(id);
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while deleting the merchant.", e);
            }
        } else {
            throw new MerchantNotFoundException(id.toString());
        }
    }

    // Map Merchant entity to Response DTO
    private MerchantResponseDTO mapToResponseDTO(Merchant merchant) {
        MerchantResponseDTO response = new MerchantResponseDTO();
        response.setId(merchant.getId());
        response.setName(merchant.getName());
        response.setPhone(merchant.getPhone());
        response.setEmail(merchant.getEmail());
        response.setCurrency(merchant.getCurrency());
        response.setAddress(merchant.getAddress());
        response.setCity(merchant.getCity());
        response.setCountry(merchant.getCountry());
        response.setPostcode(merchant.getPostcode());
        response.setCreatedAt(merchant.getCreatedAt());
        return response;
    }
}
