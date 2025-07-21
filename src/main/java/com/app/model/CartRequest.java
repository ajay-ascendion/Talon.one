package com.app.model;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for cart requests sent to rewards evaluation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequest {
    @NotNull
    private Long userId;

    @NotNull
    @Size(min = 1)
    private List<Item> items;
}
