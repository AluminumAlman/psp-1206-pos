package com.team1206.pos.order.orderCharge;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderChargeRepository extends JpaRepository<OrderCharge, Long> {

    @Query("SELECT oc FROM OrderCharge oc WHERE oc.orderId = :orderId")
    Page<OrderChargeResponseDTO> findAllWithFilters(@Param("orderId") String orderId, Pageable pageable);
}
