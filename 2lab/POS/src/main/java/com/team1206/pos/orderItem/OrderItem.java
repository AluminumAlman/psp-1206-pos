package com.team1206.pos.orderItem;

import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.order.Order;
import com.team1206.pos.service.service.Service;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order", nullable = false)
    private Order order;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product", nullable = true)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_variation", nullable = true)
    private ProductVariation productVariation;

    @ManyToOne
    @JoinColumn(name = "service", nullable = true)
    private Service service;

    // TODO: uncomment when OrderCharge is added
    // @ManyToMany(mappedBy = "order")
    // private List<OrderCharge> charges;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
