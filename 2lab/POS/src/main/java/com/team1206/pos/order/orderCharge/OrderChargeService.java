package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.enums.OrderChargeType;
import com.team1206.pos.common.enums.OrderStatus;
import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import com.team1206.pos.exceptions.UnauthorizedActionException;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.order.OrderService;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        Page<OrderCharge> orderCharges = orderChargeRepository.findAllByMerchantId(
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

    @Transactional
    public void addOrderChargeToOrder(UUID chargeId, UUID orderId) {
        OrderCharge orderCharge = getOrderChargeEntityById(chargeId);
        UUID merchantId = orderCharge.getMerchant().getId();
        userService.verifyLoggedInUserBelongsToMerchant(merchantId,
                "You are not authorized to add order charges to this order");

        Order order = orderService.getOrderEntityById(orderId);
        if (!order.getMerchant().getId().equals(merchantId))
            throw new IllegalArgumentException("Order and order charge merchants differ");

        if (order.getStatus() != OrderStatus.OPEN)
            throw new IllegalArgumentException("Order has to be open to add order charges");

        if (orderCharge.getOrders().contains(order))
            throw new IllegalArgumentException("Order charge is already applied to this order");

        orderCharge.getOrders().add(order);
        orderChargeRepository.save(orderCharge);
    }

    @Transactional
    public void removeOrderChargeFromOrder(UUID chargeId, UUID orderId) {
        OrderCharge orderCharge = getOrderChargeEntityById(chargeId);
        UUID merchantId = orderCharge.getMerchant().getId();
        userService.verifyLoggedInUserBelongsToMerchant(merchantId,
                "You are not authorized to remove order charges from this order");

        Order order = orderService.getOrderEntityById(orderId);
        if (!order.getMerchant().getId().equals(merchantId))
            throw new IllegalArgumentException("Order and order charge merchants differ");

        if (order.getStatus() != OrderStatus.OPEN)
            throw new IllegalArgumentException("Order has to be open to remove order charges");

        if (!orderCharge.getOrders().remove(order))
            throw new IllegalArgumentException("Order charge is not applied to order");

        orderChargeRepository.save(orderCharge);
    }

    // *** Helper methods ***

    public OrderCharge getOrderChargeEntityById(UUID orderChargeId) {
        return orderChargeRepository.findById(orderChargeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResourceType.ORDER_CHARGE,
                        orderChargeId.toString()));
    }

    public BigDecimal applyOrderCharges(UUID orderId, BigDecimal totalOrderItemsPrice) {
        Order order = orderService.getOrderEntityById(orderId);
        userService.verifyLoggedInUserBelongsToMerchant(
                order.getMerchant().getId(),
                "You are not authorized to calculate total order charge"
        );

        List<OrderCharge> chargeCharges = orderChargeRepository.findAllByOrderIdAndType(
                orderId,
                OrderChargeType.CHARGE
        );
        List<OrderCharge> discountCharges = orderChargeRepository.findAllByOrderIdAndType(
                orderId,
                OrderChargeType.DISCOUNT
        );

        for (OrderCharge charge : chargeCharges) {
            if (charge.getPercent() != null) {
                totalOrderItemsPrice =
                        totalOrderItemsPrice.add(totalOrderItemsPrice.multiply(new BigDecimal(charge.getPercent()).divide(
                                new BigDecimal(100))));
            }
            else {
                totalOrderItemsPrice = totalOrderItemsPrice.add(charge.getAmount());
            }
        }

        for (OrderCharge discount : discountCharges) {
            if (discount.getPercent() != null) {
                totalOrderItemsPrice =
                        totalOrderItemsPrice.subtract(totalOrderItemsPrice.multiply(new BigDecimal(discount.getPercent()).divide(
                                new BigDecimal(100))));
            }
            else {
                totalOrderItemsPrice = totalOrderItemsPrice.subtract(discount.getAmount());
            }
        }

        return totalOrderItemsPrice.setScale(2, RoundingMode.HALF_UP);
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
