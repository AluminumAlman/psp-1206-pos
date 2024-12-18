package com.team1206.pos.order.order;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(orderService.getOrders(offset, limit, status, dateFrom, dateTo));
    }

    @PostMapping
    @Operation(summary = "Create order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO requestDTO) {
        return ResponseEntity.ok(orderService.createOrder(requestDTO));
    }

    // TODO: Create an endpoint to get the total tax amount of an order

    // TODO: Create an endpoint to get the total discount amount of an order
}
