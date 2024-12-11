package com.team1206.pos.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Service, UUID> {

    @Query("SELECT s FROM Service s " +
            "WHERE (:name IS NULL OR s.name LIKE %:name%) " +
            "AND (:price IS NULL OR s.price = :price) " +
            "AND (:duration IS NULL OR s.duration = :duration)")
    Page<Service> findAllWithFilters(@Param("name") String name,
                                     @Param("price") BigDecimal price,
                                     @Param("duration") Long duration,
                                     Pageable pageable);
}
