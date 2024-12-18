package com.team1206.pos.payments.charge;

import com.team1206.pos.common.enums.ChargeType;
import com.team1206.pos.inventory.product.Product;
import com.team1206.pos.service.service.Service;
import com.team1206.pos.user.merchant.Merchant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"charge\"")
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate UUID
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ChargeType type;

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

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToMany
    @JoinTable(name = "charges_products", joinColumns = @JoinColumn(name = "charge_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products;

    @ManyToMany
    @JoinTable(name = "charges_services", joinColumns = @JoinColumn(name = "charge_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<Service> services;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    // getTotalCharge - calculates additional charges as a multiplier of the base price and a flat value from a list of charges.
    public static Pair<BigDecimal, BigDecimal> getTotalCharge(Iterable<Charge> charges) {
        BigDecimal totalMultiplier = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal HUNDRED = new BigDecimal(100);

        for (Charge charge : charges) {
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
