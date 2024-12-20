package com.team1206.pos.inventory.inventoryLog;

import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.product.ProductService;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.inventory.productVariation.ProductVariationService;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.order.OrderService;
import com.team1206.pos.order.orderItem.OrderItem;
import com.team1206.pos.user.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryLogService {
    private final InventoryLogRepository inventoryLogRepository;
    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductVariationService productVariationService;

    public InventoryLogService(InventoryLogRepository inventoryLogRepository, ProductService productService, UserService userService, OrderService orderService, ProductVariationService productVariationService) {
        this.inventoryLogRepository = inventoryLogRepository;
        this.productService = productService;
        this.orderService = orderService;
        this.productVariationService = productVariationService;
        this.userService = userService;
    }

    public InventoryLogResponseDTO createInventoryLog(CreateInventoryLogRequestDTO request) {
        UUID requestMerchantId;
        InventoryLog inventoryLog = new InventoryLog();

        if(request.getProduct() != null){
            Product product = productService.getProductEntityById(request.getProduct());
            requestMerchantId = product.getCategory().getMerchant().getId();
            userService.verifyLoggedInUserBelongsToMerchant(requestMerchantId, "You are not authorized to create inventory log for this product");

            inventoryLog.setProduct(product);
            inventoryLog.setProductVariation(null);
            inventoryLog.setType(InventoryLog.LogType.PRODUCT);
        } else if(request.getProductVariation() != null){
            ProductVariation productVariation = productVariationService.getProductVariationEntityById(request.getProductVariation());
            requestMerchantId = productVariation.getProduct().getCategory().getMerchant().getId();
            userService.verifyLoggedInUserBelongsToMerchant(requestMerchantId, "You are not authorized to create inventory log for this product variation");

            inventoryLog.setProductVariation(productVariation);
            inventoryLog.setProduct(null);
            inventoryLog.setType(InventoryLog.LogType.PRODUCT_VARIATION);
        }

        inventoryLog.setUser(userService.getCurrentUser());
        inventoryLog.setAdjustment(request.getAdjustment());
        if(request.getOrder() != null)
            inventoryLog.setOrder(orderService.getOrderEntityById(request.getOrder()));

        inventoryLogRepository.save(inventoryLog);
        return mapToResponse(inventoryLog);
    }

    public InventoryLogResponseDTO getInventoryLogById(UUID id) {
        InventoryLog inventoryLog = inventoryLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory log not found"));

        userService.verifyLoggedInUserBelongsToMerchant(inventoryLog.getUser().getMerchant().getId(), "You are not authorized to retrieve this inventory log");

        return mapToResponse(inventoryLog);
    }

    public Page<InventoryLogResponseDTO> getAllInventoryLogs(int offset, int limit, InventoryLogFilterDTO filterDTO) {
        UUID merchantId = userService.getMerchantIdFromLoggedInUser();

        Pageable pageable = PageRequest.of(offset / limit, limit); // Create Pageable object
        Page<InventoryLog> inventoryLogPage;

        if(merchantId == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first");

        inventoryLogPage = inventoryLogRepository.findAllByMerchantId(merchantId, pageable);

        // Filter the logs based on the DTO
        List<InventoryLog> filteredLogs = inventoryLogPage
                .getContent()
                .stream()
                .filter(log -> filterDTO.getProductId() == null ||
                        (log.getProduct() != null && log.getProduct().getId().equals(filterDTO.getProductId())))
                .filter(log -> filterDTO.getProductVariationId() == null ||
                        (log.getProductVariation() != null &&
                                (log.getProductVariation().getId().equals(filterDTO.getProductVariationId()) ||
                                        log.getProductVariation().getProduct().getId().equals(filterDTO.getProductId()))))
                .filter(log -> filterDTO.getOrderId() == null ||
                        (log.getOrder() != null && log.getOrder().getId().equals(filterDTO.getOrderId())))
                .filter(log -> filterDTO.getUserId() == null ||
                        (log.getUser() != null && log.getUser().getId().equals(filterDTO.getUserId())))
                .toList();

        // Map the filtered list to DTOs
        List<InventoryLogResponseDTO> responseDTOs = filteredLogs.stream()
                .map(this::mapToResponse)
                .toList();

        // Create a new Page object for the filtered results
        return new PageImpl<>(responseDTOs, pageable, filteredLogs.size());
    }

    // Service layer

    public void logOrder(Order order)
    {
        for(OrderItem orderItem : order.getItems())
        {
            if(orderItem.getProduct() != null){
                createInventoryLogForProduct(orderItem.getProduct().getId(), orderItem.getProduct().getQuantity(), order.getId());
            } else if(orderItem.getProductVariation() != null){
                createInventoryLogForProductVariation(orderItem.getProductVariation().getId(), orderItem.getProductVariation().getQuantity(), order.getId());
            }
        }
    }

    public void createInventoryLogForProduct(UUID productId, int adjustment, UUID orderId ) {
        Product product = productService.getProductEntityById(productId);
        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setProduct(product);
        inventoryLog.setProductVariation(null);
        inventoryLog.setType(InventoryLog.LogType.PRODUCT);
        inventoryLog.setUser(userService.getCurrentUser());
        if(orderId != null)
            inventoryLog.setOrder(orderService.getOrderEntityById(orderId));
        inventoryLog.setAdjustment(adjustment);
        inventoryLogRepository.save(inventoryLog);
    }

    public void createInventoryLogForProductVariation(UUID productVariationId, int adjustment, UUID orderId ) {
        ProductVariation productVariation = productVariationService.getProductVariationEntityById(productVariationId);
        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setProductVariation(productVariation);
        inventoryLog.setProduct(null);
        inventoryLog.setType(InventoryLog.LogType.PRODUCT_VARIATION);
        inventoryLog.setUser(userService.getCurrentUser());
        if(orderId != null)
            inventoryLog.setOrder(orderService.getOrderEntityById(orderId));
        inventoryLog.setAdjustment(adjustment);
        inventoryLogRepository.save(inventoryLog);
    }

    // Mappers
    private InventoryLogResponseDTO mapToResponse(InventoryLog inventoryLog) {
        InventoryLogResponseDTO response = new InventoryLogResponseDTO();
        response.setId(inventoryLog.getId());
        response.setType(inventoryLog.getType().toString());
        response.setProduct(inventoryLog.getProduct() != null ? inventoryLog.getProduct().getId() : null);
        response.setProductVariation(inventoryLog.getProductVariation() != null ? inventoryLog.getProductVariation().getId() : null);
        response.setOrder(inventoryLog.getOrder() != null ? inventoryLog.getOrder().getId() : null);
        response.setUser(inventoryLog.getUser().getId());
        response.setAdjustment(inventoryLog.getAdjustment());
        response.setCreatedAt(inventoryLog.getCreatedAt());
        return response;
    }
}
