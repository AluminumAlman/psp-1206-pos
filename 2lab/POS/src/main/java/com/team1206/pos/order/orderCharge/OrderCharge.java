package com.team1206.pos.order.orderCharge;

import com.team1206.pos.common.enums.OrderChargeType;
import com.team1206.pos.common.validation.OneOf;
import com.team1206.pos.order.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

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
    private Order order;

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

    // getTotalOrderCharge - calculates additional order charges as a multiplier of the base price and a flat value from a list of charges.
    public static Pair<BigDecimal, BigDecimal> getTotalOrderCharge(Iterable<OrderCharge> charges) {
        BigDecimal totalMultiplier = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal HUNDRED = new BigDecimal(100);

        for (OrderCharge charge : charges) {
            BigDecimal amount = charge.getAmount();
            if (amount != null)
                totalAmount = totalAmount.add(amount);

            Integer percent = charge.getPercent();
            if (percent != null)
                totalMultiplier = totalMultiplier.add(new BigDecimal(percent).divide(HUNDRED));
        }

        return Pair.of(totalMultiplier, totalAmount);
    }
}
