package com.team1206.pos.payments.charge;

import com.team1206.pos.common.enums.ChargeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ChargeRepository extends JpaRepository<Charge, UUID> {
    @Query("SELECT c FROM Charge c WHERE (:chargeType IS NULL OR c.type = :chargeType)")
    Page<Charge> findAllWithFilters(@Param("chargeType") ChargeType chargeType, Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE (:merchantId IS NULL OR c.merchant.id = :merchantId)")
    Page<Charge> findAllWithFilters(@Param("merchantId") UUID merchantId, Pageable pageable);
}
