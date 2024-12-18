package com.team1206.pos.payments.transaction;

import com.team1206.pos.common.enums.PaymentMethodType;
import com.team1206.pos.common.enums.TransactionStatus;
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

    @Query("SELECT t FROM Transaction t WHERE " + "(:orderId IS NULL OR t.order.id = :orderId) AND " + "(:paymentMethodType IS NULL OR t.paymentMethod = :paymentMethodType) AND " + "(:status IS NULL OR t.status = :status) AND " + "(:amount IS NULL OR t.amount = :amount)")
    Page<Transaction> findAllWithFilters(
            @Param("orderId") UUID orderId,
            @Param("paymentMethodType") PaymentMethodType paymentMethod,
            @Param("status") TransactionStatus status,
            @Param("amount") BigDecimal amount,
            Pageable pageable
    );

}
