package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.enums.OrderChargeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderChargeRepository extends JpaRepository<OrderCharge, UUID> {

    @Query("SELECT oc FROM OrderCharge oc WHERE oc.merchant.id = :merchantId")
    Page<OrderCharge> findAllByMerchantId(@Param("merchantId") UUID merchantId, Pageable pageable);

    @Query("SELECT oc FROM OrderCharge oc JOIN oc.orders o WHERE o.id = :orderId")
    List<OrderCharge> findAllByOrderId(@Param("orderId") UUID orderId);

    // Filter by orderId and type
    @Query("SELECT oc FROM OrderCharge oc JOIN oc.orders o WHERE o.id = :orderId AND oc.type = :type")
    List<OrderCharge> findAllByOrderIdAndType(@Param("orderId") UUID orderId, @Param("type") OrderChargeType type);
}
