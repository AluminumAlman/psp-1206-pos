package com.team1206.pos.order.orderCharge;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderChargeController {
    private final OrderChargeService orderChargeService;

    public OrderChargeController(OrderChargeService orderChargeService) {
        this.orderChargeService = orderChargeService;
    }

    @GetMapping("/charges")
    @Operation(summary = "Get all order charges from an order")
    public ResponseEntity<Page<OrderChargeResponseDTO>> getOrderCharges(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset
    ) {
        log.info("Received get order charges request: offset={} limit={}", offset, limit);

        Page<OrderChargeResponseDTO> response = orderChargeService.getOrderCharges(offset, limit);

        log.debug("Returning {} to get order charges request (offset={} limit={})", response.stream().toList(), offset, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{orderId}/charges")
    @Operation(summary = "Get all order charges from an order")
    public ResponseEntity<List<OrderChargeResponseDTO>> getOrderChargesFromOrder(
            @PathVariable String orderId
    ) {
        log.info("Received get order charges from order request: orderId={}", orderId);

        List<OrderChargeResponseDTO> response = orderChargeService.getOrderChargesFromOrder(UUID.fromString(orderId));

        log.debug("Returning {} to get order charges request (orderId={})", response.stream().toList(), orderId);
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
}
