package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.enums.OrderChargeType;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.order.OrderService;
import com.team1206.pos.user.user.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderChargeService {
    private final OrderChargeRepository orderChargeRepository;
    private final OrderService orderService;
    private final UserService userService;

    public OrderChargeService(
            OrderChargeRepository orderChargeRepository,
            OrderService orderService,
            UserService userService) {
        this.orderChargeRepository = orderChargeRepository;
        this.orderService = orderService;
        this.userService = userService;
    }

    // Get order charges
    public Page<OrderChargeResponseDTO> getOrderCharges(UUID orderId, int offset, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be greater than or equal to 0");
        }

        checkIfOrderExists(orderId);

        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(order.getMerchant().getId(), "You are not authorized to retrieve order charges");

        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<OrderCharge> orderCharges = orderChargeRepository.findAllWithFilters(
                orderId,
                pageable
        );

        return orderCharges.map(this::mapToResponseDTO);
    }

    // Create order charge
    public OrderChargeResponseDTO createOrderCharge(
            UUID orderId,
            @Valid OrderChargeRequestDTO requestBody
    ) {
        checkIfOrderExists(orderId);

        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(order.getMerchant().getId(), "You are not authorized to create an order charge");

        OrderCharge orderCharge = new OrderCharge();

        setOrderChargeFields(orderCharge, requestBody);
        orderCharge.setOrder(orderService.getOrderEntityById(orderId));

        OrderCharge savedOrderCharge = orderChargeRepository.save(orderCharge);

        return mapToResponseDTO(savedOrderCharge);
    }

    // Update order charge
    public void deleteOrderCharge(UUID orderId, UUID chargeId) {
        checkIfOrderExists(orderId);

        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(order.getMerchant().getId(), "You are not authorized to delete an order charge");

        OrderCharge orderCharge = orderChargeRepository.findById(chargeId)
                                                       .orElseThrow(() -> new ResourceNotFoundException(
                                                               ResourceType.ORDER_CHARGE,
                                                               chargeId.toString()
                                                       ));

        if (!orderCharge.getOrder().getId().equals(orderId)) {
            throw new ResourceNotFoundException(ResourceType.ORDER_CHARGE, chargeId.toString());
        }

        orderChargeRepository.delete(orderCharge);
    }


    // *** Helper methods ***

    private void checkIfOrderExists(UUID orderId) {
        orderService.getOrderEntityById(orderId);
    }

    private void setOrderChargeFields(OrderCharge orderCharge, OrderChargeRequestDTO requestBody) {
        orderCharge.setType(OrderChargeType.valueOf(requestBody.getType().toUpperCase()));
        orderCharge.setName(requestBody.getName());
        orderCharge.setPercent(requestBody.getPercent());
        orderCharge.setAmount(requestBody.getAmount());
    }

    private OrderChargeResponseDTO mapToResponseDTO(OrderCharge orderCharge) {
        OrderChargeResponseDTO responseDTO = new OrderChargeResponseDTO();

        responseDTO.setId(orderCharge.getId());
        responseDTO.setType(orderCharge.getType().name());
        responseDTO.setName(orderCharge.getName());
        responseDTO.setPercent(orderCharge.getPercent());
        responseDTO.setAmount(orderCharge.getAmount());
        responseDTO.setCreatedAt(orderCharge.getCreatedAt());
        responseDTO.setUpdatedAt(orderCharge.getUpdatedAt());

        return responseDTO;
    }
}
