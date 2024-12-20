package com.team1206.pos.inventory.inventoryLog;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/inventoryLog")
public class InventoryLogController {
    private final InventoryLogService inventoryLogService;
    public InventoryLogController(InventoryLogService inventoryLogService) {
        this.inventoryLogService = inventoryLogService;
    }

    @Operation(summary = "explicitly create new inventory log")
    @PostMapping
    public ResponseEntity<InventoryLogResponseDTO> createInventoryLog(@Valid @RequestBody CreateInventoryLogRequestDTO request) {
        InventoryLogResponseDTO response = inventoryLogService.createInventoryLog(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "retrieve inventory log by ID")
    @GetMapping("/{id}")
    public ResponseEntity<InventoryLogResponseDTO> getInventoryLog(@PathVariable UUID id) {
        InventoryLogResponseDTO responseDTO = inventoryLogService.getInventoryLogById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "retrieve all inventory logs with filters")
    @GetMapping
    public ResponseEntity<Page<InventoryLogResponseDTO>> getAllInventoryLogs(
        @RequestParam(value = "offset", defaultValue = "0") int offset,
        @RequestParam(value = "limit", defaultValue = "20") int limit,
        @Valid @RequestBody(required = false) InventoryLogFilterDTO filter) {

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must be at least 1");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be at least 0");
        }

        if (filter == null) {
            filter = new InventoryLogFilterDTO();
        }

        Page<InventoryLogResponseDTO> inventoryLogPage = inventoryLogService.getAllInventoryLogs(offset, limit, filter);

        return ResponseEntity.ok(inventoryLogPage);
    }
}
