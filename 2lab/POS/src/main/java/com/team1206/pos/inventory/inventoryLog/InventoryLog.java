package com.team1206.pos.inventory.inventoryLog;

import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.order.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"inventory_log\"")
public class InventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate UUID
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LogType type;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_variation_id", nullable = true)
    private ProductVariation productVariation;

    @OneToOne
    @JoinColumn(name = "order", nullable = true)
    private Order order;

    @Column(name = "adjustment", nullable = false)
    private Integer adjustment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum LogType {
        PRODUCT, PRODUCT_VARIATION
    }
}

