package com.team1206.pos.order.orderItem;

import com.team1206.pos.common.validation.OneOf;
import com.team1206.pos.order.order.OrderResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@OneOf(fields = {"productId", "serviceId"})
public class OrderItemController {
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get order items")
    public ResponseEntity<List<OrderItemResponseDTO>> getOrderItems(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItems(orderId));
    }

    @PostMapping("/{orderId}/items")
    @Operation(summary = "Add item to order")
    public ResponseEntity<OrderResponseDTO> addItemToOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderItemRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(orderItemService.addItemToOrder(orderId, requestDTO));
    }

    @PutMapping("/{orderId}/items/{orderItemId}")
    @Operation(summary = "Update order item")
    public ResponseEntity<OrderResponseDTO> updateOrderItem(
            @PathVariable UUID orderId,
            @PathVariable UUID orderItemId,
            @Valid @RequestBody OrderItemRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(
                orderId,
                orderItemId,
                requestDTO
        ));
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    @Operation(summary = "Delete order item")
    public ResponseEntity<OrderResponseDTO> deleteOrderItem(
            @PathVariable UUID orderId,
            @PathVariable UUID orderItemId
    ) {
        return ResponseEntity.ok(orderItemService.removeOrderItem(orderId, orderItemId));
    }
}
