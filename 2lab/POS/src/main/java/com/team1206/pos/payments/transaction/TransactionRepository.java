package com.team1206.pos.payments.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE " + "(:orderId IS NULL OR t.order.id = :orderId)")
    Page<Transaction> findAllWithFilters(@Param("orderId") UUID orderId, Pageable pageable);

    @Query(value = "SELECT * FROM transaction t WHERE " + "(:orderId IS NULL OR t.order_id = :orderId) AND " + "(:paymentMethodType IS NULL OR t.payment_method = :paymentMethodType) AND " + "(:status IS NULL OR t.status = :status) AND " + "(:amount IS NULL OR t.amount = :amount)", nativeQuery = true)
    Page<Transaction> findAllWithFilters(
            @Param("orderId") UUID orderId,
            @Param("paymentMethodType") Integer paymentMethodOrdinal,
            @Param("status") Integer statusOrdinal,
            @Param("amount") BigDecimal amount,
            Pageable pageable
    );

}
