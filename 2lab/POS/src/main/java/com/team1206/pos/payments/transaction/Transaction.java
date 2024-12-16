package com.team1206.pos.payments.transaction;

import com.team1206.pos.common.enums.PaymentMethodType;
import com.team1206.pos.common.enums.TransactionStatus;
import com.team1206.pos.order.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"transaction\"")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TransactionStatus status;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PaymentMethodType paymentMethod;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    @DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than 0.01")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
