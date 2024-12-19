package com.team1206.pos.order.orderItem;

import com.team1206.pos.common.enums.OrderStatus;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.inventory.product.ProductService;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.inventory.productVariation.ProductVariationRepository;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.order.OrderResponseDTO;
import com.team1206.pos.order.order.OrderService;
import com.team1206.pos.service.reservation.ReservationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductVariationRepository productVariationRepository;
    private final ProductService productService;
    private final OrderService orderService;
    private final ReservationService reservationService;

    public OrderItemService(
            OrderItemRepository orderItemRepository,
            ProductVariationRepository productVariationRepository,
            ProductService productService,
            @Lazy OrderService orderService,
            ReservationService reservationService
    ) {
        this.orderItemRepository = orderItemRepository;
        this.productVariationRepository = productVariationRepository;
        this.productService = productService;
        this.orderService = orderService;
        this.reservationService = reservationService;
    }

    // Get order items by order id
    public List<OrderItemResponseDTO> getOrderItems(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId)
                                  .stream()
                                  .map(this::mapToResponseDTO)
                                  .collect(Collectors.toList());
    }

    // TODO: Test out this method
    // TODO: Adjust the quantity of the order item
    // Add item to order
    public OrderResponseDTO addItemToOrder(UUID orderId, OrderItemRequestDTO requestDTO) {
        Order order = orderService.getOrderEntityById(orderId);
        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open");
        }

        OrderItem orderItem = new OrderItem();
        setOrderItemFields(orderItem, requestDTO);
        orderItem.setOrder(order);
        orderItem = orderItemRepository.save(orderItem);

        Order updatedOrder = orderService.addOrderItemToOrder(order, orderItem);

        return orderService.mapToResponseDTO(updatedOrder);
    }

    // TODO: Test out this method
    // TODO: Adjust the quantity of the order item
    // Update order item
    public OrderResponseDTO updateOrderItem(
            UUID orderId,
            UUID orderItemId,
            OrderItemRequestDTO requestDTO
    ) {
        Order order = orderService.getOrderEntityById(orderId);
        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open");
        }

        OrderItem orderItem = getOrderItemEntityById(orderItemId);
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new ResourceNotFoundException(ResourceType.ORDER_ITEM, orderItemId.toString());
        }

        setOrderItemFields(orderItem, requestDTO);
        orderItemRepository.save(orderItem);

        Order updatedOrder = orderService.replaceOrderItemInOrder(order, orderItem);

        return orderService.mapToResponseDTO(updatedOrder);
    }

    // TODO: Adjust the quantity of the order item
    // Delete order item
    public OrderResponseDTO removeOrderItem(UUID orderId, UUID orderItemId) {
        Order order = orderService.getOrderEntityById(orderId);
        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open");
        }

        OrderItem orderItem = getOrderItemEntityById(orderItemId);
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new ResourceNotFoundException(ResourceType.ORDER_ITEM, orderItemId.toString());
        }

        Order updatedOrder = orderService.removeOrderItemFromOrder(order, orderItem);

        orderItemRepository.delete(orderItem);

        return orderService.mapToResponseDTO(updatedOrder);
    }


    // *** Helper methods ***

    // Get order item by id
    public OrderItem getOrderItemEntityById(UUID id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ResourceType.ORDER_ITEM,
                                                                                                id.toString()
        ));
    }

    private void setOrderItemFields(OrderItem orderItem, OrderItemRequestDTO requestDTO) {
        if (requestDTO.getProductId() != null) {
            orderItem.setProduct(productService.getProductEntityById(requestDTO.getProductId()));
        }
        else {
            orderItem.setReservation(reservationService.getReservationEntityById(requestDTO.getReservationId()));
        }
        orderItem.setQuantity(requestDTO.getQuantity());

        UUID productVariationId = requestDTO.getProductVariationId();
        if (productVariationId != null) {
            ProductVariation productVariation = productVariationRepository.findById(
                                                                                  productVariationId)
                                                                          .orElseThrow(() -> new ResourceNotFoundException(
                                                                                  ResourceType.PRODUCT_VARIATION,
                                                                                  productVariationId.toString()
                                                                          ));
            orderItem.setProductVariation(productVariation);
        }
    }

    private OrderItemResponseDTO mapToResponseDTO(OrderItem orderItem) {
        OrderItemResponseDTO responseDTO = new OrderItemResponseDTO();
        responseDTO.setId(orderItem.getId());
        responseDTO.setProductId(orderItem.getProduct().getId());
        responseDTO.setReservationId(orderItem.getReservation().getId());
        responseDTO.setQuantity(orderItem.getQuantity());

        ProductVariation productVariation = orderItem.getProductVariation();
        responseDTO.setProductVariationId(productVariation != null ? productVariation.getId() : null);

        responseDTO.setCreatedAt(orderItem.getCreatedAt());
        responseDTO.setUpdatedAt(orderItem.getUpdatedAt());
        return responseDTO;
    }


    // TODO: Automatically adjust the quantity of the order item
}
