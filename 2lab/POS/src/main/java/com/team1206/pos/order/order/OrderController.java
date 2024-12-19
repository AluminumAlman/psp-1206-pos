package com.team1206.pos.order.order;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get paged orders")
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @RequestParam(value = "dateTo", required = false) String dateTo
    ) {
        log.info("Received get orders request: limit={} offset={} status={} dateFrom={} dateTo={}",
                limit, offset, status, dateFrom, dateTo);

        Page<OrderResponseDTO> orders = orderService.getOrders(limit, offset, status, dateFrom, dateTo);

        log.debug("Returning {} to get orders request (limit={} offset={} status={} dateFrom={} dateTo={})",
                orders.stream().toList(), limit, offset, status, dateFrom, dateTo);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    @Operation(summary = "Create order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO requestDTO) {
        log.info("Received create order request: {}", requestDTO);

        OrderResponseDTO createdOrder = orderService.createOrder(requestDTO);

        log.debug("Returning {} to create order request", createdOrder);
        // TODO: add link to newly created order
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("{orderId}")
    @Operation(summary = "Get an order")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable UUID orderId) {
        log.info("Received get order request: orderId={}", orderId);

        OrderResponseDTO response = orderService.getOrder(orderId);

        log.debug("Returning {} to get order request (orderId={})", response, orderId);
        return ResponseEntity.ok(response);
    }

    // TODO: Create an endpoint to get the total tax amount of an order

    // TODO: Create an endpoint to get the total discount amount of an order
}
