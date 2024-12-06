package com.team1206.pos.order.orderItem;

import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.orderCharge.OrderCharge;
import com.team1206.pos.service.service.Service;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"order_item\"")
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

    @ManyToMany
    @JoinTable(name = "order_items_order_charges", joinColumns = @JoinColumn(name = "order_item_id"), inverseJoinColumns = @JoinColumn(name = "order_charge_id"))
    private List<OrderCharge> charges;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
