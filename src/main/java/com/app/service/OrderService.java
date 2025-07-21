package com.app.service;

import com.app.model.Order;
import com.app.model.User;
import com.app.model.OrderRequest;
import com.app.model.CartRequest;
import com.app.model.RewardsResponse;
import com.app.repository.OrderRepository;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for order-related business logic.
 * Handles order placement, applying rewards/discounts, and updating user statistics.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserService userService;
    private final RewardsService rewardsService;
    private final OrderRepository orderRepository;

    /**
     * Places a new order, applies discounts, updates user stats, and confirms loyalty usage.
     * 
     * @param req The order request containing order details.
     * @return The saved Order entity.
     * @throws IllegalArgumentException if user does not exist or business rules are violated.
     */
    public Order placeOrder(OrderRequest req) {
        // Retrieve user
        Optional<User> userOpt = userService.getUser(req.getUserId());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found for ID: " + req.getUserId());
        }
        User user = userOpt.get();

        // Evaluate discounts and rewards
        CartRequest cartRequest = new CartRequest(req.getUserId(), req.getItems());
        RewardsResponse rewardsResponse = rewardsService.evaluateCart(cartRequest);

        // Calculate final total after applying discounts
        double discount = rewardsResponse.getDiscountAmount();
        double originalTotal = req.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        double finalTotal = Math.max(0, originalTotal - discount);

        // Create Order entity
        Order order = new Order();
        order.setUser(user);
        order.setItems(req.getItems());
        order.setTotalAmount(finalTotal);
        order.setDiscountApplied(discount);
        order.setStatus("PLACED");

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Update user statistics
        user.setTotalOrders(user.getTotalOrders() + 1);
        user.setTotalSpent(user.getTotalSpent() + finalTotal);
        userService.save(user);

        // Confirm loyalty point usage if applicable
        rewardsService.confirmLoyalty(user.getId(), finalTotal);

        return savedOrder;
    }

    /**
     * Saves an order based on the incoming order request.
     * 
     * @param req The order request.
     * @return The saved Order entity.
     */
    public Order saveOrder(OrderRequest req) {
        return placeOrder(req);
    }
}
