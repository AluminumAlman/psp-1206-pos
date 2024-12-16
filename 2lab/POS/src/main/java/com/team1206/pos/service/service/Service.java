package com.team1206.pos.service.service;

import com.team1206.pos.payments.charge.Charge;
import com.team1206.pos.payments.discount.Discount;
import com.team1206.pos.service.reservation.Reservation;
import com.team1206.pos.user.merchant.Merchant;
import com.team1206.pos.user.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "\"service\"")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    @DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than 0.01")
    private BigDecimal price;

    @Column(name = "duration", nullable = false)
    private Long duration;

    @ManyToMany
    @JoinTable(name = "services_users", joinColumns = @JoinColumn(name = "service_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> employees;

    @ManyToMany(mappedBy = "services")
    private List<Charge> charges;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    @ManyToMany(mappedBy = "services")
    private List<Discount> discounts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
