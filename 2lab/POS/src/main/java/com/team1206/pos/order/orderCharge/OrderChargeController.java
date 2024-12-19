package com.team1206.pos.order.orderCharge;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
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
        log.info("Received get order charges request: orderId={} offset={} limit={}", orderId, offset, limit);

        Page<OrderChargeResponseDTO> response = orderChargeService.getOrderCharges(orderId, offset, limit);

        log.debug("Returning {} to get order charges request (orderId={} offset={} limit={})", response.stream().toList(), orderId, offset, limit);
        return ResponseEntity.ok(response);
    }

    @PostMapping("{orderId}/charges")
    @Operation(summary = "Create order charge")
    public ResponseEntity<OrderChargeResponseDTO> createOrderCharge(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderChargeRequestDTO requestBody
    ) {
        log.info("Received create order charge request: orderId={} {}", orderId, requestBody);

        OrderChargeResponseDTO response = orderChargeService.createOrderCharge(orderId, requestBody);

        log.debug("Returning {} to create order charge request (orderId={})", response, orderId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
