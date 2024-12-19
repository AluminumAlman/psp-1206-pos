package com.team1206.pos.order.orderItem;

import com.team1206.pos.common.enums.OrderStatus;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.IllegalRequestException;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.product.ProductService;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.inventory.productVariation.ProductVariationRepository;
import com.team1206.pos.inventory.productVariation.ProductVariationService;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.order.OrderResponseDTO;
import com.team1206.pos.order.order.OrderService;
import com.team1206.pos.service.reservation.Reservation;
import com.team1206.pos.service.reservation.ReservationService;
import com.team1206.pos.user.user.UserService;
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
    private final UserService userService;
    private final ProductVariationService productVariationService;

    public OrderItemService(
            OrderItemRepository orderItemRepository,
            ProductVariationRepository productVariationRepository,
            ProductService productService,
            @Lazy OrderService orderService,
            ReservationService reservationService,
            UserService userService,
            ProductVariationService productVariationService
    ) {
        this.orderItemRepository = orderItemRepository;
        this.productVariationRepository = productVariationRepository;
        this.productService = productService;
        this.orderService = orderService;
        this.reservationService = reservationService;
        this.userService = userService;
        this.productVariationService = productVariationService;
    }

    // Get order items by order id
    public List<OrderItemResponseDTO> getOrderItems(UUID orderId) {
        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to view items in this order"
        );

        return orderItemRepository.findByOrderId(orderId)
                                  .stream()
                                  .map(this::mapToResponseDTO)
                                  .collect(Collectors.toList());
    }

    // Add item to order
    public OrderResponseDTO addItemToOrder(UUID orderId, CreateOrderItemRequestDTO requestDTO) {
        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to add items to this order"
        );

        checkCreateRequestDTO(requestDTO);

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open");
        }


        if (requestDTO.getProductId() != null) {
            adjustQuantityOrderItemAdd(requestDTO);
        }

        OrderItem orderItem = new OrderItem();

        setOrderItemFields(orderItem, requestDTO);
        orderItem.setOrder(order);
        orderItem = orderItemRepository.save(orderItem);
        Order updatedOrder = orderService.addOrderItemToOrder(order, orderItem);

        return orderService.mapToResponseDTO(updatedOrder);
    }

    // Update order item
    public OrderResponseDTO updateOrderItem(
            UUID orderId,
            UUID orderItemId,
            UpdateOrderItemRequestDTO requestDTO
    ) {
        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to update items in this order"
        );

        if (requestDTO.getQuantity() <= 0) {
            throw new IllegalRequestException("Quantity must be greater than zero");
        }

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open");
        }

        OrderItem orderItem = getOrderItemEntityById(orderItemId);
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new ResourceNotFoundException(ResourceType.ORDER_ITEM, orderItemId.toString());
        }

        if (orderItem.getReservation() != null) {
            throw new IllegalRequestException("Cannot update reservation order item quantity");
        }


        int quantityDiff = orderItem.getQuantity() - requestDTO.getQuantity();
        adjustQuantityOrderItemUpdate(orderItem, quantityDiff);

        orderItem.setQuantity(requestDTO.getQuantity());
        orderItemRepository.save(orderItem);
        Order updatedOrder = orderService.replaceOrderItemInOrder(order, orderItem);

        return orderService.mapToResponseDTO(updatedOrder);
    }

    // Delete order item
    public OrderResponseDTO removeOrderItem(UUID orderId, UUID orderItemId) {
        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to remove items from this order"
        );
        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open");
        }

        OrderItem orderItem = getOrderItemEntityById(orderItemId);
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new ResourceNotFoundException(ResourceType.ORDER_ITEM, orderItemId.toString());
        }

        adjustQuantityOrderItemRemove(orderItem);

        Order updatedOrder = orderService.removeOrderItemFromOrder(order, orderItem);
        orderItemRepository.delete(orderItem);

        return orderService.mapToResponseDTO(updatedOrder);
    }

    // Cancel order item
    public void cancelOrderItem(UUID orderId, UUID orderItemId) {
        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to cancel items in this order"
        );

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new IllegalStateException("Order is not open");
        }

        OrderItem orderItem = getOrderItemEntityById(orderItemId);
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new ResourceNotFoundException(ResourceType.ORDER_ITEM, orderItemId.toString());
        }

        adjustQuantityOrderItemRemove(orderItem);

        orderItem.setQuantity(0);
        orderItemRepository.save(orderItem);
    }


    // *** Helper methods ***

    public void deleteOrderItem(OrderItem orderItem) {
        orderItemRepository.delete(orderItem);
    }

    private void checkCreateRequestDTO(CreateOrderItemRequestDTO requestDTO) {
        if (requestDTO.getQuantity() <= 0) {
            throw new IllegalRequestException("Quantity must be greater than zero");
        }
        if (requestDTO.getProductId() == null && requestDTO.getReservationId() == null) {
            throw new IllegalRequestException("Either productId or reservationId must be provided");
        }
        if (requestDTO.getReservationId() != null && requestDTO.getProductVariationId() != null) {
            throw new IllegalRequestException(
                    "Order reservation item cannot be paired with product variation");
        }
    }

    private void adjustQuantityOrderItemAdd(CreateOrderItemRequestDTO orderItem) {
        if (orderItem.getProductVariationId() != null) {
            productVariationService.adjustProductVariationQuantity(
                    orderItem.getProductVariationId(),
                    -orderItem.getQuantity()
            );
        }
        else {
            productService.adjustProductQuantity(
                    orderItem.getProductId(),
                    -orderItem.getQuantity()
            );
        }
    }

    private void adjustQuantityOrderItemUpdate(OrderItem orderItem, int quantityDiff) {
        if (orderItem.getProductVariation() != null) {
            productVariationService.adjustProductVariationQuantity(
                    orderItem.getProductVariation().getId(),
                    quantityDiff
            );
        }
        else if (orderItem.getProduct() != null) {
            productService.adjustProductQuantity(orderItem.getProduct().getId(), quantityDiff);
        }
    }

    private void adjustQuantityOrderItemRemove(OrderItem orderItem) {
        if (orderItem.getProductVariation() != null) {
            productVariationService.adjustProductVariationQuantity(
                    orderItem.getProductVariation().getId(),
                    orderItem.getQuantity()
            );
        }
        else if (orderItem.getProduct() != null) {
            productService.adjustProductQuantity(orderItem.getProduct().getId(), orderItem.getQuantity());
        }
    }

    // Get order item by id
    public OrderItem getOrderItemEntityById(UUID id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ResourceType.ORDER_ITEM,
                                                                                                id.toString()
        ));
    }

    private void setOrderItemFields(OrderItem orderItem, CreateOrderItemRequestDTO requestDTO) {
        if (requestDTO.getProductId() != null) {
            orderItem.setProduct(productService.getProductEntityById(requestDTO.getProductId()));
            orderItem.setQuantity(requestDTO.getQuantity());
        }
        else {
            orderItem.setReservation(reservationService.getReservationEntityById(requestDTO.getReservationId()));
            orderItem.setQuantity(1);
        }

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

        Product product = orderItem.getProduct();
        responseDTO.setProductId(product != null ? product.getId() : null);

        Reservation reservation = orderItem.getReservation();
        responseDTO.setReservationId(reservation != null ? reservation.getId() : null);
        responseDTO.setQuantity(orderItem.getQuantity());

        ProductVariation productVariation = orderItem.getProductVariation();
        responseDTO.setProductVariationId(productVariation != null ? productVariation.getId() : null);

        responseDTO.setCreatedAt(orderItem.getCreatedAt());
        responseDTO.setUpdatedAt(orderItem.getUpdatedAt());
        return responseDTO;
    }
}
