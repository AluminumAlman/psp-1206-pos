package com.team1206.pos.user.merchant;

import com.team1206.pos.inventory.productCategory.ProductCategory;
import com.team1206.pos.order.order.Order;
import com.team1206.pos.order.orderCharge.OrderCharge;
import com.team1206.pos.payments.charge.Charge;
import com.team1206.pos.payments.discount.Discount;
import com.team1206.pos.service.schedule.Schedule; // Import the Schedule entity
import com.team1206.pos.service.service.Service;
import com.team1206.pos.user.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "\"merchant\"")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate UUID
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", length = 20, nullable = true)
    private String phone;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "currency", nullable = false, length = 10) // (e.g., "USD")
    private String currency;

    @Column(name = "address", length = 255, nullable = true)
    private String address;

    @Column(name = "city", length = 50, nullable = true)
    private String city;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "postcode", length = 20, nullable = true)
    private String postcode;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discount> discounts;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Service> services;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Charge> charges;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategory> productCategories;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderCharge> orderCharges;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
