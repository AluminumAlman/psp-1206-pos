package com.team1206.pos.payments.discount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DiscountRepository extends JpaRepository<Discount, UUID> {
    @Query("SELECT d FROM Discount d " +
            "WHERE (d.merchant = :merchantId " +
            "AND d.isActive " +
            "AND (not(:validOnly) " +
            "OR d.validFrom <= cast(:now as timestamp) AND cast(:now as timestamp) < d.validUntil))")
    Page<Discount> findAllWithFilters(
            @Param("merchantId") UUID merchantId,
            @Param("validOnly") boolean validOnly,
            @Param("now") LocalDateTime now,
            Pageable pageable);
}
