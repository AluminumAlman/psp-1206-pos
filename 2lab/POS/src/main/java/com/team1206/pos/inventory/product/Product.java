package com.team1206.pos.inventory.product;

import com.team1206.pos.common.enums.DiscountScope;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.payments.charge.Charge;
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
@Table(name = "\"product\"")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate UUID
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // TODO: nuspresti ar verta sita laikyti; galbut naudinga cache-avimui, taciau tuomet reikia papildomos logikos.
    // @Column(name = "variation", nullable = false)
    // private boolean variation;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariation> variations;

    @ManyToOne
    @JoinColumn(name = "category", nullable = false)
    private ProductCategory category;

    @ManyToMany(mappedBy = "products")
    private List<Charge> charges;

    @ManyToMany(mappedBy = "products")
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

    /// Gets the discounts which should affect this product's and its variations' prices.
    public List<Discount> getEffectiveDiscountsFor(LocalDateTime now, DiscountScope scope) {
        Stream<Discount> discounts = category.getEffectiveDiscountsFor(now, scope).stream();
        return Stream.concat(
                    discounts,
                    this.discounts.stream().filter(discount -> discount.getScope() == scope && discount.isActiveAndValid(now)))
                .collect(Collectors.toMap(Discount::getId, p -> p, (p, q) -> p)).values().stream().toList();
    }
}