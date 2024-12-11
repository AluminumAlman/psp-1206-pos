package com.team1206.pos.inventory.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR p.name LIKE %:name%) AND " +
            "(:price IS NULL OR p.price = :price) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> findAllWithFilters(@Param("name") String name,
                                     @Param("price") BigDecimal price,
                                     @Param("categoryId") UUID categoryId,
                                     Pageable pageable);
}
