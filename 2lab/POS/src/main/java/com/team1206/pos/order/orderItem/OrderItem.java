package com.team1206.pos.order.orderItem;

import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.inventory.productVariation.ProductVariation;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.orderCharge.OrderCharge;
import com.team1206.pos.payments.discount.Discount;
import com.team1206.pos.service.reservation.Reservation;
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
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_variation_id", nullable = true)
    private ProductVariation productVariation;

    // TODO: PAKEISTI SĄRYŠĮ Į OneToOne su Reservation
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = true)
    private Service service;
    // TODO: IR PAKEISTI ŠITUO
//    @OneToOne
//    @JoinColumn(name = "reservation_id", nullable = true)
//    private Reservation reservation;

    // TODO: PAŠALINTI ŠĮ SĄRYŠĮ
    @ManyToMany
    @JoinTable(name = "order_items_order_charges", joinColumns = @JoinColumn(name = "order_item_id"), inverseJoinColumns = @JoinColumn(name = "order_charge_id"))
    private List<OrderCharge> charges;

    @ManyToMany
    @JoinTable(name = "order_items_discounts", joinColumns = @JoinColumn(name = "order_item_id"), inverseJoinColumns = @JoinColumn(name = "discount_id"))
    private List<Discount> discounts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
