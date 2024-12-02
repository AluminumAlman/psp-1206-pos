package com.team1206.pos.inventory.inventory;

import com.team1206.pos.inventory.inventoryLog.InventoryLog;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate UUID
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "product", nullable = true)
    private Product product;

    @OneToOne
    @JoinColumn(name = "productVariation", nullable = true)
    private ProductVariation productVariation;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @OneToMany(mappedBy = "inventory")
    private List<InventoryLog> inventoryLogs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
