package com.app.controller;

import com.app.model.User;
import com.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /users/{id} : Fetch user details by ID.
     *
     * @param id the ID of the user to fetch
     * @return 200 OK with user details, or 404 Not Found if user does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUser(id)
                .map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * PUT /users/{id} : Update user's totalOrders and totalSpent.
     *
     * @param id the ID of the user to update
     * @param request validated request body containing totalOrders and totalSpent
     * @return 204 No Content on success, 400 Bad Request on validation error, 404 Not Found if user does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody com.app.model.UpdateUserRequest request) {

        boolean updated = userService.updateUserTotals(id, request.getTotalOrders(), request.getTotalSpent());
        if (!updated) {
            // User not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // Successfully updated
        return ResponseEntity.noContent().build();
    }
}
