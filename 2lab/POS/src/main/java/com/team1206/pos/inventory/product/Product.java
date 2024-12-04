package com.team1206.pos.inventory.product;

import com.team1206.pos.inventory.inventory.Inventory;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.payments.charge.Charge;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
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

    @Column(name = "price", nullable = false)
    private Integer price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariation> variations;

    @ManyToOne
    @JoinColumn(name = "category", nullable = false)
    private ProductCategory category;

    @ManyToMany(mappedBy = "products")
    private List<Charge> charges;

    @OneToOne(mappedBy = "product")
    private Inventory inventory;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
