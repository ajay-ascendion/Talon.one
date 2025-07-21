package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Order entity representing a placed order.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many orders can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // CascadeType.ALL ensures items are persisted with the order
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    private double totalAmount;

    private double discountApplied;

    private String status; // e.g., PLACED, CANCELLED, etc.
}
