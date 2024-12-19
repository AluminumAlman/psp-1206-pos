package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.enums.OrderChargeType;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.order.OrderService;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.user.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public Page<OrderChargeResponseDTO> getOrderCharges(int offset, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be greater than or equal to 0");
        }

        UUID merchantId = userService.getMerchantIdFromLoggedInUser();

        Pageable pageable = PageRequest.of(offset / limit, limit);

        Page<OrderCharge> orderCharges = null; orderChargeRepository.findAllByMerchantId(
                merchantId,
                pageable
        );

        return orderCharges.map(this::mapToResponseDTO);
    }

    public List<OrderChargeResponseDTO> getOrderChargesFromOrder(UUID orderId) {
        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(order.getMerchant().getId(), "You are not authorized to view charges from this order");

        List<OrderCharge> orderCharges = orderChargeRepository.findAllByOrderId(orderId);

        return orderCharges.stream().map(this::mapToResponseDTO).toList();
    }

    // Create order charge
    public OrderChargeResponseDTO createOrderCharge(
            OrderChargeRequestDTO requestBody
    ) {
        Merchant merchant = userService.getCurrentUser().getMerchant();
        if (merchant == null)
            throw new UnauthorizedActionException("Super-admin has to be logged in to create order charge");

        OrderCharge orderCharge = new OrderCharge();

        setOrderChargeFields(orderCharge, requestBody);
        orderCharge.setMerchant(merchant);
        OrderCharge savedOrderCharge = orderChargeRepository.save(orderCharge);

        return mapToResponseDTO(savedOrderCharge);
    }


    // *** Helper methods ***

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
