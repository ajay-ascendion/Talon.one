package com.app.service;

import com.app.model.CartRequest;
import com.app.model.RewardsResponse;
import com.app.talonone.TalonOneClient;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service layer for handling rewards and discount logic.
 * Integrates with Talon.One API for evaluating and confirming rewards.
 */
@Service
@RequiredArgsConstructor
public class RewardsService {

    private final TalonOneClient talonOneClient;

    /**
     * Evaluates rewards and discounts for the given cart by interacting with Talon.One.
     * 
     * @param req The cart request containing user and item details.
     * @return RewardsResponse containing discount and reward information.
     */
    public RewardsResponse evaluateCart(CartRequest req) {
        // Update user profile in Talon.One
        talonOneClient.updateProfile(req.getUserId());

        // Evaluate session (cart) in Talon.One
        return talonOneClient.evaluateSession(req);
    }

    /**
     * Confirms the usage of loyalty points for a user and order total.
     * 
     * @param userId The ID of the user.
     * @param total The total amount of the order.
     */
    public void confirmLoyalty(Long userId, double total) {
        talonOneClient.confirmLoyalty(userId, total);
    }

    /**
     * Evaluates rewards for an order request (for controller compatibility).
     * 
     * @param orderRequest The order request.
     * @return RewardsResponse with evaluated rewards.
     */
    public RewardsResponse evaluateRewards(com.app.model.OrderRequest orderRequest) {
        CartRequest cartRequest = new CartRequest(orderRequest.getUserId(), orderRequest.getItems());
        return evaluateCart(cartRequest);
    }
}
