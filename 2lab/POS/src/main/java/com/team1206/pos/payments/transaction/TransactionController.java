package com.team1206.pos.payments.transaction;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(transactionService.getTransactions(limit, offset, orderId));
    }
}
