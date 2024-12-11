package com.team1206.pos.inventory.productCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
}
