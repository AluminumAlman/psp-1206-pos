package com.team1206.pos.order.order;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        return ResponseEntity.ok(orderService.getOrders(offset, limit, status, dateFrom, dateTo));
    }

    @PostMapping
    @Operation(summary = "Create order")
    public ResponseEntity<OrderResponseDTO> createOrder() {
        return ResponseEntity.ok(orderService.createOrder());
    }

    @PostMapping("{orderId}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable UUID orderId) {
        log.info("Received cancel order request: orderId={}", orderId);

        OrderResponseDTO response = orderService.cancelOrder(orderId);

        log.debug("Returning {} to cancel order request (orderId={})", response, orderId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{orderId}")
    @Operation(summary = "Delete an order")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        log.info("Received delete order request: orderId={}", orderId);

        orderService.deleteOrder(orderId);

        log.debug("Returning 204 NO CONTENT to delete order request (orderId={})", orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{orderId}/totalPrice")
    @Operation(summary = "Get the total amount of an order")
    public ResponseEntity<BigDecimal> getTotal(@PathVariable UUID orderId) {
        log.debug("Received get total amount request: orderId={}", orderId);

        BigDecimal total = orderService.calculateTotalProductAndServicePrice(orderId);

        log.debug("Returning {} to get total amount request (orderId={})", total, orderId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("{orderId}/finalCheckoutAmount")
    @Operation(summary = "Get the final checkout amount of an order")
    public ResponseEntity<BigDecimal> getFinalCheckoutAmount(@PathVariable UUID orderId) {
        log.debug("Received get final checkout amount request: orderId={}", orderId);

        BigDecimal finalCheckoutAmount = orderService.calculateFinalCheckoutAmount(orderId);

        log.debug("Returning {} to get final checkout amount request (orderId={})", finalCheckoutAmount, orderId);
        return ResponseEntity.ok(finalCheckoutAmount);
    }

    @PostMapping("{orderId}/setTip")
    @Operation(summary = "Set order tip")
    public ResponseEntity<OrderResponseDTO> setTip(@PathVariable UUID orderId, @RequestBody BigDecimal tipAmount) {
        log.info("Received set order tip request: orderId={} {}", orderId, tipAmount);

        if (tipAmount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Tip amount must not be negative");

        tipAmount = tipAmount.setScale(2, RoundingMode.FLOOR);
        OrderResponseDTO response = orderService.setTip(orderId, tipAmount);

        log.debug("Returning {} to set order tip request (orderId={})", response, orderId);
        return ResponseEntity.ok(response);
    }
}
