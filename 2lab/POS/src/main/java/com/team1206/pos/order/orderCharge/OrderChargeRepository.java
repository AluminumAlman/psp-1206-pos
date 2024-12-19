package com.team1206.pos.order.orderCharge;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OrderChargeRepository extends JpaRepository<OrderCharge, UUID> {

    //@Query("SELECT oc FROM OrderCharge oc WHERE oc.order.id = :orderId")
    //Page<OrderCharge> findAllWithFilters(@Param("orderId") UUID orderId, Pageable pageable);
}
