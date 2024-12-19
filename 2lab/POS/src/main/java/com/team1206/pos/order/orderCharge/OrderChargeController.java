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

    @PostMapping("charges")
    @Operation(summary = "Create order charge")
    public ResponseEntity<OrderChargeResponseDTO> createOrderCharge(
            @Valid @RequestBody OrderChargeRequestDTO requestBody
    ) {
        log.info("Received create order charge request: {}", requestBody);

        OrderChargeResponseDTO response = orderChargeService.createOrderCharge(requestBody);

        log.debug("Returning {} to create order charge request", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("{orderId}/charges/{chargeId}")
    @Operation(summary = "Add order charge to order")
    public ResponseEntity<Void> addOrderChargeToOrder(@PathVariable UUID orderId, @PathVariable UUID chargeId) {
        log.info("Received add order charge to order request: orderId={} chargeId={}", orderId, chargeId);

        orderChargeService.addOrderChargeToOrder(chargeId, orderId);

        log.debug("Returning 200 OK to add order charge to order request (orderId={} chargeId={})", orderId, chargeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{orderId}/charges/{chargeId}")
    @Operation(summary = "Remove order charge from order")
    public ResponseEntity<Void> removeOrderChargeFromOrder(@PathVariable UUID orderId, @PathVariable UUID chargeId) {
        log.info("Received remove order charge from order request: orderId={} chargeId={}", orderId, chargeId);

        orderChargeService.removeOrderChargeFromOrder(chargeId, orderId);

        log.debug("Returning 204 No content to remove order charge from order request (orderId={} chargeId={})", orderId, chargeId);
        return ResponseEntity.noContent().build();
    }
}
