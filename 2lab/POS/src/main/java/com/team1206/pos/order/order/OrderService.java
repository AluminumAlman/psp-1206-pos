package com.team1206.pos.order.order;

import com.team1206.pos.common.enums.OrderStatus;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.order.orderCharge.OrderCharge;
import com.team1206.pos.order.orderItem.OrderItem;
import com.team1206.pos.order.orderItem.OrderItemService;
import com.team1206.pos.payments.discount.Discount;
import com.team1206.pos.payments.transaction.Transaction;
import com.team1206.pos.user.merchant.MerchantService;
import com.team1206.pos.user.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final MerchantService merchantService;
    private final OrderItemService orderItemService;

    public OrderService(
            OrderRepository orderRepository,
            UserService userService,
            MerchantService merchantService,
            OrderItemService orderItemService
    ) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.merchantService = merchantService;
        this.orderItemService = orderItemService;
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
    public OrderResponseDTO createOrder(OrderRequestDTO requestBody) {
        UUID merchantId = requestBody.getMerchantId();
        userService.verifyLoggedInUserBelongsToMerchant(merchantId, "You are not authorized to create an order");

        Order order = new Order();

        setOrderFields(order, requestBody);

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

    private void setOrderFields(Order order, OrderRequestDTO requestBody) {
        order.setMerchant(merchantService.getMerchantEntityById(requestBody.getMerchantId()));
        order.setStatus(OrderStatus.OPEN);

        List<OrderItem> orderItems = requestBody.getOrderItems()
                                                .stream()
                                                .map(orderItemService::getOrderItemEntityById)
                                                .toList();

        order.setItems(orderItems);
    }

    public OrderResponseDTO mapToResponseDTO(Order order) {
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();

        orderResponseDTO.setId(order.getId());
        orderResponseDTO.setStatus(String.valueOf(order.getStatus()));

        List<UUID> orderCharges = order.getCharges().stream().map(OrderCharge::getId).toList();
        orderResponseDTO.setCharges(orderCharges);

        List<UUID> orderItems = order.getItems().stream().map(OrderItem::getId).toList();
        orderResponseDTO.setItems(orderItems);

        List<UUID> transactions = order.getTransactions().stream().map(Transaction::getId).toList();

        orderResponseDTO.setTransactions(transactions);
        orderResponseDTO.setMerchantId(order.getMerchant().getId());

        List<UUID> discounts = order.getDiscounts().stream().map(Discount::getId).toList();
        orderResponseDTO.setDiscounts(discounts);

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
