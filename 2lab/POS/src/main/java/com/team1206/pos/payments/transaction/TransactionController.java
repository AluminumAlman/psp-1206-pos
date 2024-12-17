package com.team1206.pos.payments.transaction;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "Get paged transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "orderId", required = false) String orderId
    ) {
        log.info("Received get transactions request: limit={} offset={} orderId={}", limit, offset, orderId);

        Page<TransactionResponseDTO> transactions = transactionService.getTransactions(limit, offset, orderId);

        log.debug("Returning {} to get transactions request (limit={} offset={} orderId={})", transactions.stream().toList(), limit, offset, orderId);
        return ResponseEntity.ok(transactions);
    }
}
