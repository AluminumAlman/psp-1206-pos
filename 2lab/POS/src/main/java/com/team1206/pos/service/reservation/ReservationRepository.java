package com.team1206.pos.service.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    @Query("SELECT r FROM Reservation r " +
            "WHERE (:serviceName IS NULL OR r.service.name LIKE %:serviceName%) " +
            "AND (:customerName IS NULL OR CONCAT(r.firstName, ' ', r.lastName) LIKE %:customerName%) " +
            "AND (:customerEmail IS NULL OR r.employee.email LIKE %:customerEmail%) " +
            "AND (:customerPhone IS NULL OR r.phone LIKE %:customerPhone%) " +
            "AND (:appointedAt IS NULL OR r.appointedAt = :appointedAt)")
    Page<Reservation> findAllWithFilters(
            @Param("serviceName") String serviceName,
            @Param("customerName") String customerName,
            @Param("customerEmail") String customerEmail,
            @Param("customerPhone") String customerPhone,
            @Param("appointedAt") LocalDateTime appointedAt,
            Pageable pageable);
}
