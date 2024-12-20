package com.team1206.pos.service.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    @Query("SELECT r FROM Reservation r " +
            "WHERE (r.service.merchant = :merchantId) " +
            "AND (:serviceName IS NULL OR r.service.name LIKE %:serviceName%) " +
            "AND (:customerName IS NULL OR CONCAT(r.firstName, ' ', r.lastName) LIKE %:customerName%) " +
            "AND (:customerEmail IS NULL OR r.employee.email LIKE %:customerEmail%) " +
            "AND (:customerPhone IS NULL OR r.phone LIKE %:customerPhone%) " +
            "AND (cast(:appointedAt as timestamp) IS NULL OR r.appointedAt = :appointedAt)")
    Page<Reservation> findAllWithFilters(
            @Param("serviceName") String serviceName,
            @Param("customerName") String customerName,
            @Param("customerEmail") String customerEmail,
            @Param("customerPhone") String customerPhone,
            @Param("appointedAt") LocalDateTime appointedAt,
            @Param("merchantId") UUID merchantId,
            Pageable pageable);

    @Query("SELECT r FROM Reservation r " +
            "WHERE (r.service.merchant = :merchantId) " +
            "AND r.employee.id = :userId " +
            "AND r.appointedAt >= :startOfDay " +
            "AND r.appointedAt < :endOfDay")
    List<Reservation> findReservationsByEmployeeAndDate(@Param("userId") UUID userId,
                                                        @Param("startOfDay") LocalDateTime startOfDay,
                                                        @Param("endOfDay") LocalDateTime endOfDay,
                                                        @Param("merchantId") UUID merchantId);
}
