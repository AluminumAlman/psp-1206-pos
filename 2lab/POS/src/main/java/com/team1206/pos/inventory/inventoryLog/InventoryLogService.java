package com.team1206.pos.inventory.inventoryLog;

import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.product.ProductService;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.inventory.productVariation.ProductVariationService;
import com.team1206.pos.order.order.OrderService;
import com.team1206.pos.user.user.UserService;
import org.springframework.stereotype.Service;

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

    // Service layer

    public void createInventoryLogForProduct(UUID productId, int adjustment, UUID orderId ) {
        Product product = productService.getProductEntityById(productId);
        InventoryLog inventoryLog = new InventoryLog();
        inventoryLog.setProduct(product);
        inventoryLog.setProductVariation(null);
        inventoryLog.setType(InventoryLog.LogType.PRODUCT);
        inventoryLog.setUser(userService.getCurrentUser());
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
