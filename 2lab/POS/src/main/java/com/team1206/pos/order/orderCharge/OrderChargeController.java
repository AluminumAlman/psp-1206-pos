package com.team1206.pos.order.orderCharge;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderChargeController {
    private final OrderChargeService orderChargeService;

    public OrderChargeController(OrderChargeService orderChargeService) {
        this.orderChargeService = orderChargeService;
    }

    @GetMapping("{orderId}/charges")
    @Operation(summary = "Get order charges")
    public ResponseEntity<Page<OrderChargeResponseDTO>> getOrderCharges(
            @PathVariable UUID orderId,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(orderChargeService.getOrderCharges(orderId, offset, limit));
    }

    @PostMapping("{orderId}/charges")
    @Operation(summary = "Create order charge")
    public ResponseEntity<OrderChargeResponseDTO> createOrderCharge(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderChargeRequestDTO requestBody
    ) {
        return ResponseEntity.ok(orderChargeService.createOrderCharge(orderId, requestBody));
    }

    @DeleteMapping("{orderId}/charges/{chargeId}")
    @Operation(summary = "Delete order charge")
    public ResponseEntity<Void> deleteOrderCharge(
            @PathVariable UUID orderId,
            @PathVariable UUID chargeId
    ) {
        orderChargeService.deleteOrderCharge(orderId, chargeId);
        return ResponseEntity.noContent().build();
    }
}
