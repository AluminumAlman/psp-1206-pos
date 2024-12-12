package com.team1206.pos.payments.charge;

import com.team1206.pos.common.enums.ChargeScope;
import com.team1206.pos.common.enums.ChargeType;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.service.service.Service;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ChargeService {
    private final ChargeRepository chargeRepository;
    MerchantRepository merchantRepository;

    public ChargeService(ChargeRepository chargeRepository, MerchantRepository merchantRepository) {
        this.chargeRepository = chargeRepository;
        this.merchantRepository = merchantRepository;
    }

    public List<ChargeResponseDTO> getCharges() {
        return chargeRepository.findAll()
                               .stream()
                               .map(this::mapToResponseDTO)
                               .collect(Collectors.toList());
    }

    public ChargeResponseDTO createCharge(ChargeRequestDTO request) {
        Merchant merchant = merchantRepository.findById(request.getMerchantId())
                                              .orElseThrow(() -> new ResourceNotFoundException(ResourceType.MERCHANT, request.getMerchantId()
                                                                                                                             .toString()));

        Charge charge = mapToEntity(request, merchant);
        Charge savedCharge = chargeRepository.save(charge);

        return mapToResponseDTO(savedCharge);
    }

    private Charge mapToEntity(ChargeRequestDTO request, Merchant merchant) {
        Charge charge = new Charge();
        charge.setType(ChargeType.valueOf(request.getChargeType().toUpperCase()));
        charge.setScope(ChargeScope.valueOf(request.getChargeScope().toUpperCase()));
        charge.setName(request.getName());
        charge.setPercent(request.getPercent());
        charge.setAmount(request.getAmount());
        charge.setMerchant(merchant);

        // Convert product UUIDs to Product entities
        List<Product> products = request.getProducts()
                                        .stream()
                                        .map(productId -> {
                                            Product product = new Product();
                                            product.setId(productId);
                                            return product;
                                        })
                                        .collect(Collectors.toList());
        charge.setProducts(products);

        // Convert service UUIDs to Service entities
        List<Service> services = request.getServices()
                                        .stream()
                                        .map(serviceId -> {
                                            Service service = new Service();
                                            service.setId(serviceId);
                                            return service;
                                        })
                                        .collect(Collectors.toList());
        charge.setServices(services);

        charge.setCreatedAt(LocalDateTime.now());
        charge.setIsActive(true);

        return charge;
    }

    private ChargeResponseDTO mapToResponseDTO(Charge charge) {
        ChargeResponseDTO responseDTO = new ChargeResponseDTO();
        responseDTO.setId(charge.getId());
        responseDTO.setChargeType(charge.getType().name());
        responseDTO.setChargeScope(charge.getScope().name());
        responseDTO.setName(charge.getName());
        responseDTO.setPercent(charge.getPercent());
        responseDTO.setAmount(charge.getAmount());
        responseDTO.setMerchantId(charge.getMerchant().getId());
        responseDTO.setCreatedAt(charge.getCreatedAt());
        responseDTO.setUpdatedAt(charge.getUpdatedAt());
        responseDTO.setProducts(charge.getProducts()
                                      .stream()
                                      .map(Product::getId)
                                      .toArray(UUID[]::new));
        responseDTO.setServices(charge.getServices()
                                      .stream()
                                      .map(Service::getId)
                                      .toArray(UUID[]::new));
        return responseDTO;
    }
}
