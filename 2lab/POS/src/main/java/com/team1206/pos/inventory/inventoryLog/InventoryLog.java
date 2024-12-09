package com.team1206.pos.inventory.inventoryLog;

import com.team1206.pos.inventory.inventory.Inventory;
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

    @ManyToOne
    @JoinColumn(name = "inventory", nullable = false)
    private Inventory inventory;

    @OneToOne
    @JoinColumn(name = "order", nullable = true)
    private Order order;

    @Column(name = "adjustment", nullable = false)
    private Integer adjustment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}