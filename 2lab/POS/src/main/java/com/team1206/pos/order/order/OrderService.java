package com.team1206.pos.order.order;

import com.team1206.pos.common.enums.ResourceType;
import com.team1206.pos.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException(ResourceType.ORDER,
                                                                                                 orderId.toString()
        ));
    }

    // Service layer

    public Order getOrderEntityById(UUID orderId){
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ORDER, orderId.toString()));
    }
}
