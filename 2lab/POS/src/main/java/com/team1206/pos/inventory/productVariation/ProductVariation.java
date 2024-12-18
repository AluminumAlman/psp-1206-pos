package com.team1206.pos.inventory.productVariation;

import com.team1206.pos.common.enums.DiscountScope;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.payments.discount.Discount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "\"product_variation\"")
public class ProductVariation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate UUID
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @ManyToOne
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @ManyToMany(mappedBy = "productVariations")
    private List<Discount> discounts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    /// Gets the discounts which should affect this product variation's price.
    public List<Discount> getEffectiveDiscountsFor(LocalDateTime now, DiscountScope scope) {
        Stream<Discount> discounts = product.getEffectiveDiscountsFor(now, scope).stream();
        return Stream.concat(
                    discounts,
                    this.discounts.stream().filter(discount -> discount.getScope() == scope && discount.isActiveAndValid(now)))
                .collect(Collectors.toMap(Discount::getId, p -> p, (p, q) -> p)).values().stream().toList();
    }
}
