package com.team1206.pos.inventory.productVariation;

import com.team1206.pos.inventory.inventory.Inventory;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.payments.discount.Discount;
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

    @ManyToOne
    @JoinColumn(name = "product", nullable = false)
    private Product product;

    @OneToOne(mappedBy = "productVariation")
    private Inventory inventory;

    @ManyToMany(mappedBy = "productVariations")
    private List<Discount> discounts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
