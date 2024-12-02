package com.team1206.pos.order;

import com.team1206.pos.orderItem.OrderItem;
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
public class Order {
    enum Status {
        Open,
        Closed,
        Refunded,
        Cancelled,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    // TODO: uncomment when OrderCharge is added
    // @ManyToMany(mappedBy = "order")
    // private List<OrderCharge> charges;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;

    // TODO: uncomment when Transaction is added
    // @OneToMany(mappedBy = "order")
    // private List<Transaction> transactions;

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
