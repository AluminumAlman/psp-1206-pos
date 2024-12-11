package com.team1206.pos.inventory.productVariation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, UUID> {
}
