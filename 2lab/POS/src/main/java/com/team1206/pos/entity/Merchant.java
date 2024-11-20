package com.team1206.pos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
@Entity

public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Auto-generate UUID
    @Column(name = "id", updatable = false, nullable = false) // non-updatable and non-nullable
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "currency", nullable = false, length = 10) // (e.g., "USD")
    private String currency;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "postcode", length = 20)
    private String postcode;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discount> discounts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
