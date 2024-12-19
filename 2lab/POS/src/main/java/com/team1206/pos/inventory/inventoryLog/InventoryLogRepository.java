package com.team1206.pos.inventory.inventoryLog;

import com.team1206.pos.inventory.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, UUID> {
    @Query("SELECT il FROM InventoryLog il WHERE il.user.merchant.id = :merchantId")
    Page<InventoryLog> findAllByMerchantId(@Param("merchantId") UUID merchantId, Pageable pageable);
}
