package com.team1206.pos.order.order;

import com.team1206.pos.common.enums.OrderStatus;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.IllegalRequestException;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.inventory.inventoryLog.InventoryLogService;
import com.team1206.pos.order.orderCharge.OrderCharge;
import com.team1206.pos.order.orderCharge.OrderChargeService;
import com.team1206.pos.order.orderItem.OrderItem;
import com.team1206.pos.order.orderItem.OrderItemService;
import com.team1206.pos.payments.discount.Discount;
import com.team1206.pos.payments.transaction.Transaction;
import com.team1206.pos.user.merchant.MerchantService;
import com.team1206.pos.user.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final MerchantService merchantService;
    private final OrderItemService orderItemService;
    private final OrderChargeService orderChargeService;
    private final InventoryLogService inventoryLogService;

    public OrderService(
            OrderRepository orderRepository,
            UserService userService,
            MerchantService merchantService,
            OrderItemService orderItemService,
            @Lazy OrderChargeService orderChargeService,
            @Lazy InventoryLogService inventoryLogService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.merchantService = merchantService;
        this.orderItemService = orderItemService;
        this.orderChargeService = orderChargeService;
        this.inventoryLogService = inventoryLogService;
    }

    // Get paged orders
    public Page<OrderResponseDTO> getOrders(
            int offset,
            int limit,
            String status,
            String dateFrom,
            String dateTo
    ) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be greater than or equal to 0");
        }

        UUID merchantId = userService.getMerchantIdFromLoggedInUser();

        if(merchantId == null)
            throw new UnauthorizedActionException("Super-admin has to be assigned to Merchant first");

        OrderStatus orderStatus =
                (status != null && !status.isEmpty()) ? OrderStatus.valueOf(status.toUpperCase()) : null;

        LocalDateTime parsedDateFrom = (dateFrom == null || dateFrom.isEmpty()) ? LocalDateTime.of(1970,
                                                                                                   1,
                                                                                                   1,
                                                                                                   0,
                                                                                                   0
        ) : LocalDateTime.parse(dateFrom);

        LocalDateTime parsedDateTo =
                (dateTo == null || dateTo.isEmpty()) ? LocalDateTime.now() : LocalDateTime.parse(
                        dateTo);

        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<Order> orders = orderRepository.findAllWithFilters(
                merchantId,
                orderStatus,
                parsedDateFrom,
                parsedDateTo,
                pageable
        );

        return orders.map(this::mapToResponseDTO);
    }

    // Create order
    public OrderResponseDTO createOrder() {
        UUID userMerchantId = userService.getMerchantIdFromLoggedInUser();

        Order order = new Order();

        order.setMerchant(merchantService.getMerchantEntityById(userMerchantId));
        setOrderFields(order);

        Order savedOrder = orderRepository.save(order);

        return mapToResponseDTO(savedOrder);
    }


    // Cancel order
    public OrderResponseDTO cancelOrder(UUID orderId) {
        Order order = getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to cancel order"
        );
        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order has to be open to be cancelled");
        }

        order.getItems().forEach(item -> orderItemService.cancelOrderItem(orderId, item.getId()));

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);

        return mapToResponseDTO(updatedOrder);
    }

    // Delete order
    public void deleteOrder(UUID orderId) {
        Order order = getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to delete order"
        );

        order.getItems().forEach(orderItemService::deleteOrderItem);

        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponseDTO setTip(UUID orderId, BigDecimal tipAmount) {
        Order order = getOrderEntityById(orderId);

        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to set tip");

        if (order.getStatus() != OrderStatus.OPEN)
            throw new IllegalRequestException("Order has to be open to set tip");

        order.setTip(tipAmount);
        orderRepository.save(order);
        return mapToResponseDTO(order);
    }

    // Service layer

    public Order closeOrder(UUID orderId) {
        Order order = getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to close order"
        );

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order has to be open to be closed");
        }

        order.setStatus(OrderStatus.CLOSED);
        orderRepository.save(order);

        inventoryLogService.logOrder(order);

        return order;
    }

    // *** Helper methods ***

    public Order addOrderItemToOrder(Order order, OrderItem orderItem) {
        order.getItems().add(orderItem);

        return orderRepository.save(order);
    }

    public Order replaceOrderItemInOrder(Order order, OrderItem orderItem) {
        order.getItems().removeIf(item -> item.getId().equals(orderItem.getId()));
        order.getItems().add(orderItem);

        return orderRepository.save(order);
    }

    public Order removeOrderItemFromOrder(Order order, OrderItem orderItem) {
        order.getItems().removeIf(item -> item.getId().equals(orderItem.getId()));

        return orderRepository.save(order);
    }

    public BigDecimal calculateTotalProductAndServicePrice(UUID orderId) {
        Order order = getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(order.getMerchant().getId(), "You are not authorized to get total amount of this order");

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            totalAmount = totalAmount.add(orderItemService.getTotalPrice(item));
        }

        return totalAmount;
    }

    public BigDecimal calculateFinalCheckoutAmount(UUID orderId) {
        BigDecimal totalOrderItemsPrice = calculateTotalProductAndServicePrice(orderId);

        return orderChargeService.applyOrderCharges(orderId, totalOrderItemsPrice);
    }

    private void setOrderFields(Order order) {
        order.setStatus(OrderStatus.OPEN);

        // if orderItems is empty, set items to empty list
        order.setItems(List.of());
    }

    public OrderResponseDTO mapToResponseDTO(Order order) {
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        orderResponseDTO.setId(order.getId());
        orderResponseDTO.setStatus(String.valueOf(order.getStatus()));

        List<OrderCharge> charges = order.getCharges();
        orderResponseDTO.setCharges(charges == null ? List.of() : charges.stream().map(OrderCharge::getId).toList());

        List<OrderItem> orderItems = order.getItems();
        orderResponseDTO.setItems(orderItems == null ? List.of() : orderItems.stream().map(OrderItem::getId).toList());

        List<Transaction> transactions = order.getTransactions();
        orderResponseDTO.setTransactions(transactions == null ? List.of() : transactions.stream().map(Transaction::getId).toList());

        orderResponseDTO.setMerchantId(order.getMerchant().getId());

        List<Discount> discounts = order.getDiscounts();
        orderResponseDTO.setDiscounts(discounts == null ? List.of() : discounts.stream().map(Discount::getId).toList());

        orderResponseDTO.setCreatedAt(order.getCreatedAt());
        orderResponseDTO.setUpdatedAt(order.getUpdatedAt());

        return orderResponseDTO;
    }

    // Service layer

    public Order getOrderEntityById(UUID orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ORDER, orderId.toString()));
    }
}
