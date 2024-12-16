package com.team1206.pos.payments.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:orderId IS NULL OR t.order.id = :orderId)")
    Page<Transaction> findAllWithFilters(@Param("orderId") String orderId, Pageable pageable);
}
