package com.team1206.pos.inventory.inventoryLog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, UUID> {
}
