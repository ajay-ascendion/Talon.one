package com.app.model;

import lombok.*;

/**
 * DTO for updating user profile in Talon.One.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private String userId;
    private int totalOrders;
    private double totalSpent;
    // Add additional profile fields as needed for Talon.One
}
