package com.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * User entity representing a customer in the e-commerce application.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalOrders;

    private double totalSpent;

    // Additional user profile fields can be added as needed (e.g., email, name)
}
