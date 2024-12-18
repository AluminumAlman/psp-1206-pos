package com.team1206.pos.payments.discount;

import com.team1206.pos.common.validation.OneOf;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.service.service.Service;
import com.team1206.pos.user.merchant.Merchant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"discount\"")
@OneOf(fields = {"percent", "amount"})
public class Discount {
    public enum Scope {
        ORDER,
        ORDER_ITEM,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 255, name = "name", nullable = false)
    private String name;

    @Column(name = "percent", nullable = true)
    private Integer percent;

    @Column(name = "amount", nullable = true, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "scope", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Scope scope;

    @ManyToMany
    @JoinTable(name = "discounts_services",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<Service> services;

    @ManyToMany
    @JoinTable(name = "discounts_product_categories",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "product_category_id"))
    private List<ProductCategory> productCategories;

    @ManyToMany
    @JoinTable(name = "discounts_products",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    @ManyToMany
    @JoinTable(name = "discounts_product_variations",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "product_variation_id"))
    private List<ProductVariation> productVariations;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValidFor(LocalDateTime now) {
        return validFrom.isBefore(now) && validUntil.isAfter(now);
    }
}
