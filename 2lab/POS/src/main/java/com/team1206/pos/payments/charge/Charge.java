package com.team1206.pos.payments.charge;

import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.service.service.Service;
import com.team1206.pos.user.merchant.Merchant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Charge {
    public enum Type {
        Tax,
        Service,
    }

    public enum Scope {
        Order,
        Product,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate UUID
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Type type;

    @Column(name = "scope", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Scope scope;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "percent", nullable = true)
    private Integer percent;

    @Column(name = "amount", nullable = true)
    private Integer amount;

    @ManyToMany
    @JoinTable(name = "charges_products", joinColumns = @JoinColumn(name = "charge_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    @ManyToMany
    @JoinTable(name = "charges_services", joinColumns = @JoinColumn(name = "charge_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<Service> services;

    @ManyToOne
    @JoinColumn(name = "merchant", nullable = false)
    private Merchant merchant;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
