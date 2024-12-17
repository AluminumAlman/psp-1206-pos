package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.enums.OrderChargeType;
import com.team1206.pos.common.validation.OneOf;
import com.team1206.pos.order.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"order_charge\"")
@OneOf(fields = {"percent", "amount"})
public class OrderCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private OrderChargeType type;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "percent", nullable = true)
    private Integer percent;

    @Column(name = "amount", nullable = true, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
