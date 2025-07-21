package com.app.service;

import com.app.model.User;
import com.app.repository.UserRepository;
lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service layer for user-related business logic.
 * Handles user retrieval and updates to user statistics.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Fetches a user by their unique ID.
     * 
     * @param id The ID of the user.
     * @return An Optional containing the User if found, or empty if not found.
     */
    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Updates user statistics such as total orders and total spent.
     * 
     * @param user The user entity with updated statistics.
     * @return The updated user entity.
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates the user's totalOrders and totalSpent.
     * 
     * @param id The ID of the user to update.
     * @param totalOrders The new total number of orders.
     * @param totalSpent The new total spent amount.
     * @return true if the user was updated, false if not found.
     */
    public boolean updateUserTotals(Long id, int totalOrders, double totalSpent) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTotalOrders(totalOrders);
            user.setTotalSpent(totalSpent);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * Updates user statistics after a successful order placement.
     * 
     * @param userId The ID of the user.
     * @param order The order that was placed.
     */
    public void updateUserAfterOrder(Long userId, com.app.model.Order order) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTotalOrders(user.getTotalOrders() + 1);
            user.setTotalSpent(user.getTotalSpent() + order.getTotalAmount());
            userRepository.save(user);
        }
    }
}
