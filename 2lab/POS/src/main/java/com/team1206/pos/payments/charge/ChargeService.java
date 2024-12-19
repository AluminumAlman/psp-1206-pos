package com.team1206.pos.payments.charge;

import com.team1206.pos.common.enums.ChargeScope;
import com.team1206.pos.common.enums.ChargeType;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.product.ProductService;
import com.team1206.pos.service.service.Service;
import com.team1206.pos.service.service.ServiceService;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.merchant.MerchantService;
import com.team1206.pos.user.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ChargeService {
    private final ChargeRepository chargeRepository;
    private final MerchantService merchantService;
    private final UserService userService;
    private final ProductService productService;
    private final ServiceService serviceService;

    public ChargeService(ChargeRepository chargeRepository,
                         MerchantService merchantService,
                         UserService userService,
                         ProductService productService,
                         ServiceService serviceService) {
        this.chargeRepository = chargeRepository;
        this.merchantService = merchantService;
        this.userService = userService;
        this.productService = productService;
        this.serviceService = serviceService;
    }

    // Get charges by merchantId paginated
    public Page<ChargeResponseDTO> getCharges(int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();
        if(merchantId == null)
            throw new UnauthorizedActionException("User is not assigned to Merchant");
        Page<Charge> chargePage = chargeRepository.findAllWithFilters(merchantId, pageable);

        return chargePage.map(this::mapToResponseDTO);
    }

    // Get charges by chargeType paginated
    public Page<ChargeResponseDTO> getCharges(int limit, int offset, String chargeType) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();
        if(merchantId == null)
            throw new UnauthorizedActionException("User is not assigned to Merchant");

        Page<Charge> chargePage = chargeRepository.findAllWithFilters(
                ChargeType.valueOf(chargeType.toUpperCase()),
                merchantId,
                pageable);

        return chargePage.map(this::mapToResponseDTO);
    }

    // Create charge
    public ChargeResponseDTO createCharge(ChargeRequestDTO request) {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();
        if(merchantId == null)
            throw new UnauthorizedActionException("User is not assigned to Merchant");
        Merchant merchant = merchantService.getMerchantEntityById(merchantId);

        Charge charge = mapToEntity(request, merchant);
        Charge savedCharge = chargeRepository.save(charge);

        return mapToResponseDTO(savedCharge);
    }

    // Retrieve charge by ID
    public ChargeResponseDTO getChargeById(UUID chargeId) {
        Charge charge = getChargeEntityById(chargeId);

        userService.verifyLoggedInUserBelongsToMerchant(charge.getMerchant().getId(), "You are not authorized to retrieve this charge");

        return mapToResponseDTO(charge);
    }

    // Update charge by ID
    public ChargeResponseDTO updateCharge(UUID chargeId, ChargeRequestDTO request) {
        Charge charge = getChargeEntityById(chargeId);

        userService.verifyLoggedInUserBelongsToMerchant(charge.getMerchant().getId(), "You are not authorized to update this charge");

        setChargeFieldsFromRequestDTO(charge, request);

        Charge updatedCharge = chargeRepository.save(charge);

        return mapToResponseDTO(updatedCharge);
    }

    // Deactivate charge by ID
    public void deactivateCharge(UUID chargeId) {
        Charge charge = getChargeEntityById(chargeId);

        userService.verifyLoggedInUserBelongsToMerchant(charge.getMerchant().getId(), "You are not authorized to deactivate this charge");

        charge.setIsActive(false);

        chargeRepository.save(charge);
    }

    // Reactivate charge by ID
    public ChargeResponseDTO reactivateCharge(UUID chargeId) {
        Charge charge = getChargeEntityById(chargeId);

        userService.verifyLoggedInUserBelongsToMerchant(charge.getMerchant().getId(), "You are not authorized to reactivate this charge");

        charge.setIsActive(true);

        Charge updatedCharge = chargeRepository.save(charge);

        return mapToResponseDTO(updatedCharge);
    }

    // Get charges of a product
    public List<ChargeResponseDTO> getChargesOfProduct(UUID productId) {
        Product product = productService.getProductEntityById(productId);
        userService.verifyLoggedInUserBelongsToMerchant(
                product.getCategory().getMerchant().getId(),
                "You are not authorized to get charges of this product");

        return product.getCharges().stream().map(this::mapToResponseDTO).toList();
    }

    // Add charge to product
    @Transactional
    public void addChargeToProduct(UUID chargeId, UUID productId) {
        Product product = productService.getProductEntityById(productId);
        UUID merchantId = product.getCategory().getMerchant().getId();
        userService.verifyLoggedInUserBelongsToMerchant(
                merchantId,
                "You are not authorized to add charges to this product");

        Charge charge = getChargeEntityById(chargeId);
        if (merchantId != charge.getMerchant().getId())
            throw new IllegalRequestException("Product and charge merchants differ");

        if (charge.getProducts().contains(product))
            throw new IllegalRequestException("Charge is already applied to product");

        charge.getProducts().add(product);
        chargeRepository.save(charge);
    }

    // Remove charge from product
    @Transactional
    public void removeChargeFromProduct(UUID chargeId, UUID productId) {
        Product product = productService.getProductEntityById(productId);
        UUID merchantId = product.getCategory().getMerchant().getId();
        userService.verifyLoggedInUserBelongsToMerchant(
                merchantId,
                "You are not authorized to remove charges from this product");

        Charge charge = getChargeEntityById(chargeId);
        if (merchantId != charge.getMerchant().getId())
            throw new IllegalRequestException("Product and charge merchants differ");

        if (!charge.getProducts().remove(product))
            throw new IllegalRequestException("Charge is not applied to product");

        chargeRepository.save(charge);
    }

    // Get charges of a service
    public List<ChargeResponseDTO> getChargesOfService(UUID serviceId) {
        Service service = serviceService.getServiceEntityById(serviceId);
        userService.verifyLoggedInUserBelongsToMerchant(
                service.getMerchant().getId(),
                "You are not authorized to get charges of this service");

        return service.getCharges().stream().map(this::mapToResponseDTO).toList();
    }

    // Add charge to service
    @Transactional
    public void addChargeToService(UUID chargeId, UUID serviceId) {
        Service service = serviceService.getServiceEntityById(serviceId);
        UUID merchantId = service.getMerchant().getId();
        userService.verifyLoggedInUserBelongsToMerchant(
                merchantId,
                "You are not authorized to add charges to this service");

        Charge charge = getChargeEntityById(chargeId);
        if (merchantId != charge.getMerchant().getId())
            throw new IllegalRequestException("Service and charge merchants differ");

        if (charge.getProducts().contains(service))
            throw new IllegalRequestException("Charge is already applied to service");

        charge.getServices().add(service);
        chargeRepository.save(charge);
    }

    // Remove charge from service
    @Transactional
    public void removeChargeFromService(UUID chargeId, UUID serviceId) {
        Service service = serviceService.getServiceEntityById(serviceId);
        UUID merchantId = service.getMerchant().getId();
        userService.verifyLoggedInUserBelongsToMerchant(
                merchantId,
                "You are not authorized to remove charges from this service");

        Charge charge = getChargeEntityById(chargeId);
        if (merchantId != charge.getMerchant().getId())
            throw new IllegalRequestException("Service and charge merchants differ");

        if (!charge.getServices().remove(service))
            throw new IllegalRequestException("Charge is not applied to service");

        chargeRepository.save(charge);
    }

    // Service layer
    public List<Charge> getAllEntitiesById(List<UUID> chargeIds) {
        return chargeRepository.findAllById(chargeIds);
    }


    // *** Helper methods ***

    public Charge getChargeEntityById(UUID chargeId) {
        Charge charge =  chargeRepository.findById(chargeId).orElseThrow(() -> new ResourceNotFoundException(
                ResourceType.CHARGE,
                chargeId.toString()));
        return charge;
    }

    public ResponseEntity<Page<ChargeResponseDTO>> handleGetChargesRequest(
            int limit,
            int offset,
            Supplier<Page<ChargeResponseDTO>> serviceCall) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }

        Page<ChargeResponseDTO> response = serviceCall.get();
        return ResponseEntity.ok(response);
    }

    private void setChargeFieldsFromRequestDTO(Charge charge, ChargeRequestDTO request) {
        charge.setType(ChargeType.valueOf(request.getChargeType().toUpperCase()));
        charge.setScope(ChargeScope.valueOf(request.getChargeScope().toUpperCase()));
        charge.setName(request.getName());
        charge.setPercent(request.getPercent());
        charge.setAmount(request.getAmount());

//         Convert product UUIDs to Product entities
        List<Product> products = request.getProducts().stream().map(productId -> {
            Product product = new Product();
            product.setId(productId);
            return product;
        }).collect(Collectors.toList());
        charge.setProducts(products);

//         Convert service UUIDs to Service entities
        List<Service> services = request.getServices().stream().map(serviceId -> {
            Service service = new Service();
            service.setId(serviceId);
            return service;
        }).collect(Collectors.toList());
        charge.setServices(services);
    }

    private Charge mapToEntity(ChargeRequestDTO request, Merchant merchant) {
        Charge charge = new Charge();

        setChargeFieldsFromRequestDTO(charge, request);
        charge.setMerchant(merchant);
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
        responseDTO.setIsActive(charge.getIsActive());
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
