package com.team1206.pos.order.order;

import com.team1206.pos.common.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o WHERE (:merchantId IS NULL OR o.merchant.id = :merchantId) AND (:status IS NULL OR o.status = :status) AND (o.createdAt >= :dateFrom) AND (o.createdAt <= :dateTo)")
    Page<Order> findAllWithFilters(
            @Param("merchantId") UUID merchantId,
            @Param("status") OrderStatus status,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable
    );
}
