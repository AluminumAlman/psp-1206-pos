package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.enums.OrderChargeType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderChargeService {
    private final OrderChargeRepository orderChargeRepository;

    public OrderChargeService(OrderChargeRepository orderChargeRepository) {
        this.orderChargeRepository = orderChargeRepository;
    }

    public Page<OrderChargeResponseDTO> getOrderCharges(String orderId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);

        return orderChargeRepository.findAllWithFilters(orderId, pageable);
    }

    public OrderChargeResponseDTO createOrderCharge(
            String orderId,
            @Valid OrderChargeRequestDTO requestBody
    ) {
        OrderCharge orderCharge = new OrderCharge();
//        orderCharge.setOrderId(orderId);
//        orderCharge.setType(requestBody.getType());
//        orderCharge.setName(requestBody.getName());
//        orderCharge.setPercent(requestBody.getPercent());
//        orderCharge.setAmount(requestBody.getAmount());

        OrderCharge savedOrderCharge = orderChargeRepository.save(orderCharge);

        return mapToResponseDTO(savedOrderCharge);
    }


    // *** Helper methods ***

//    private void setOrderChargeFields(OrderCharge orderCharge, OrderChargeRequestDTO requestBody) {
//        orderCharge.setType(OrderChargeType.valueOf(requestBody.getType()));
//        orderCharge.setName(requestBody.getName());
//        orderCharge.setOrderId(requestBody.getOrderId());
//        orderCharge.setPercent(requestBody.getPercent());
//        orderCharge.setAmount(requestBody.getAmount());
//    }

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
