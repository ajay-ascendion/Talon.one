package com.app.model;

import lombok.*;
import java.util.List;

/**
 * DTO for rewards response from Talon.One or internal evaluation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardsResponse {
    private double discountAmount;
    private List<String> appliedRewards; // e.g., list of applied campaign names
    private int loyaltyPointsUsed;
    private int loyaltyPointsEarned;
    private String message; // Optional message for the user

    /**
     * Factory method for error responses.
     */
    public static RewardsResponse error(String message) {
        return RewardsResponse.builder()
                .discountAmount(0)
                .appliedRewards(List.of())
                .loyaltyPointsUsed(0)
                .loyaltyPointsEarned(0)
                .message(message)
                .build();
    }
}
