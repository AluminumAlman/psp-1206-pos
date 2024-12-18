package com.team1206.pos.payments.discount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DiscountRepository extends JpaRepository<Discount, UUID> {
    @Query("SELECT s FROM Discount s " +
            "WHERE (s.isActive = true " +
            "AND s.merchant = :merchantId " +
            "AND (:onlyValid = false OR (s.validFrom <= now() AND now() < s.validUntil)))")
    Page<Discount> findAllWithFilters(@Param("onlyValid") boolean onlyValid,
                                      @Param("merchantId") UUID merchantId,
                                      Pageable pageable);
}
