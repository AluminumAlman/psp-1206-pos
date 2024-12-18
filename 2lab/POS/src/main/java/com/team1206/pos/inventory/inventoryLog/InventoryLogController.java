package com.team1206.pos.inventory.inventoryLog;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/inventoryLog")
public class InventoryLogController {
    private final InventoryLogService inventoryLogService;
    public InventoryLogController(InventoryLogService inventoryLogService) {
        this.inventoryLogService = inventoryLogService;
    }

    @PostMapping
    public ResponseEntity<InventoryLogResponseDTO> createInventoryLog(@Valid @RequestBody CreateInventoryLogRequestDTO request) {
        InventoryLogResponseDTO response = inventoryLogService.createInventoryLog(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryLogResponseDTO> getInventoryLog(@PathVariable String id) {
        InventoryLogResponseDTO responseDTO = inventoryLogService.getInventoryLogById(UUID.fromString(id));
        return ResponseEntity.ok(responseDTO);
    }
}
