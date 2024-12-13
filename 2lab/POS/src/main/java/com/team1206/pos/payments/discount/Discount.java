package com.team1206.pos.payments.discount;

import com.team1206.pos.user.merchant.Merchant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"discount\"")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(length = 255, name = "name", nullable = false)
    private String name;

    @Column(name = "percent", nullable = true)
    private Integer percent;

    @Column(name = "amount", nullable = true)
    private Integer amount;

    @Column(name = "valid_from", nullable = true)
    private LocalDateTime validFrom;

    @Column(name = "valid_until", nullable = true)
    private LocalDateTime validUntil;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
