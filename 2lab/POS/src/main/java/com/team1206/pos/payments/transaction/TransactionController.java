package com.team1206.pos.payments.transaction;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{orderId}/transactions")
    @Operation(summary = "Get paged transactions for order")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(
            @PathVariable UUID orderId,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(transactionService.getTransactions(limit, offset, orderId));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get paged transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "orderId", required = false) UUID orderId,
            @RequestParam(value = "paymentMethodType", required = false) String paymentMethodType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "amount", required = false) BigDecimal amount
    ) {
        log.info("Received get transactions request: limit={} offset={} orderId={}", limit, offset, orderId, paymentMethodType, status, amount);

        Page<TransactionResponseDTO> transactions = transactionService.getTransactions(limit, offset, orderId, paymentMethodType, status, amount);

        log.debug("Returning {} to get transactions request (limit={} offset={} orderId={})", transactions.stream().toList(), limit, offset, orderId, paymentMethodType, status, amount);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/{orderId}/transactions")
    @Operation(summary = "Create transaction for order")
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @PathVariable UUID orderId,
            @RequestBody TransactionRequestDTO requestBody
    ) {
        return ResponseEntity.ok(transactionService.createTransaction(orderId, requestBody));
    }

    @GetMapping("/{orderId}/transactions/{transactionId}")
    @Operation(summary = "Get transaction details for order")
    public ResponseEntity<TransactionResponseDTO> getTransaction(
            @PathVariable UUID orderId,
            @PathVariable UUID transactionId
    ) {
        return ResponseEntity.ok(transactionService.getTransaction(orderId, transactionId));
    }

    @PatchMapping("/{orderId}/transactions/{transactionId}/complete")
    @Operation(summary = "Mark cash transaction as completed")
    public ResponseEntity<TransactionResponseDTO> completeTransaction(
            @PathVariable UUID orderId,
            @PathVariable UUID transactionId
    ) {
        return ResponseEntity.ok(transactionService.completeCashTransaction(
                orderId,
                transactionId
        ));
    }

    @PatchMapping("/{orderId}/transactions/{transactionId}/refund")
    @Operation(summary = "Refund transaction")
    public ResponseEntity<TransactionResponseDTO> refundTransaction(
            @PathVariable UUID orderId,
            @PathVariable UUID transactionId
    ) {
        return ResponseEntity.ok(transactionService.refundTransaction(orderId, transactionId));
    }
}
