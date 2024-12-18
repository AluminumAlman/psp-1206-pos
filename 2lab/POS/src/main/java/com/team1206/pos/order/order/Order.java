package com.team1206.pos.order.order;

import com.team1206.pos.common.enums.DiscountScope;
import com.team1206.pos.common.enums.OrderStatus;
import com.team1206.pos.order.orderCharge.OrderCharge;
import com.team1206.pos.order.orderItem.OrderItem;
import com.team1206.pos.payments.discount.Discount;
import com.team1206.pos.payments.transaction.Transaction;
import com.team1206.pos.user.merchant.Merchant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderCharge> charges;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @ManyToMany
    @JoinTable(name = "orders_discounts", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "discount_id"))
    private List<Discount> discounts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public List<Discount> getDiscountsByItemsAt(LocalDateTime now) {
        Stream<Discount> discountStream = Stream.empty();
        for (OrderItem item : items) {
            if (item.getReservation() != null)
                discountStream = Stream.concat(discountStream, item.getReservation().getService().getEffectiveDiscountsFor(now, DiscountScope.ORDER).stream());

            if (item.getProduct() != null)
                discountStream = Stream.concat(discountStream, item.getProduct().getEffectiveDiscountsFor(now, DiscountScope.ORDER).stream());

            if (item.getProductVariation() != null)
                discountStream = Stream.concat(discountStream, item.getProductVariation().getEffectiveDiscountsFor(now, DiscountScope.ORDER).stream());
        }
        return discountStream.collect(Collectors.toMap(Discount::getId, p -> p, (p, q) -> p)).values().stream().toList();
    }
}
