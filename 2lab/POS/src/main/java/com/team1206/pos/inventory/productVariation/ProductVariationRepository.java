package com.team1206.pos.inventory.productVariation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, UUID> {
    @Query("SELECT pv FROM ProductVariation pv " +
            "WHERE pv.product.id = :productId " +
            "AND (:merchantId IS NULL OR pv.product.category.merchant.id = :merchantId)")
    List<ProductVariation> findAllWithFilters(@Param("productId") UUID productId,
                                              @Param("merchantId") UUID merchantId);

}
