package com.app.model;

import lombok.*;
import java.util.List;

/**
 * DTO for session (cart) evaluation in Talon.One.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {
    private String userId;
    private List<Item> items;
    private double cartTotal;
    // Additional session fields as required by Talon.One
}
