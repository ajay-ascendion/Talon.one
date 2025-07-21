package com.app.controller;

import com.app.model.OrderRequest;
import com.app.model.OrderResponse;
import com.app.model.Order;
import com.app.service.OrderService;
import com.app.service.RewardsService;
import com.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * REST controller for handling order placement.
 * Integrates with RewardsService and UserService as part of the order workflow.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final RewardsService rewardsService;
    private final UserService userService;

    /**
     * Places a new order.
     * 
     * @param orderRequest the incoming order request, validated
     * @return ResponseEntity with order details and appropriate HTTP status
     */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest orderRequest) {
        try {
            // Evaluate rewards for the order (e.g., discounts, loyalty points)
            rewardsService.evaluateRewards(orderRequest);

            // Save the order and get the persisted order entity
            Order savedOrder = orderService.saveOrder(orderRequest);

            // Update user information based on the order (e.g., loyalty status)
            userService.updateUserAfterOrder(orderRequest.getUserId(), savedOrder);

            // Build response DTO
            OrderResponse orderResponse = OrderResponse.fromOrder(savedOrder);

            // Build location URI for the created resource
            URI location = URI.create("/orders/" + savedOrder.getId());

            // Return 201 Created with order details
            return ResponseEntity
                    .created(location)
                    .body(orderResponse);

        } catch (IllegalArgumentException ex) {
            // Handle known bad request scenarios (e.g., invalid business logic)
            logger.warn("Bad request during order placement: {}", ex.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(OrderResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            // Handle unexpected errors gracefully
            logger.error("Unexpected error during order placement", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(OrderResponse.error("An unexpected error occurred. Please try again later."));
        }
    }

    /**
     * Handles validation errors and returns a 400 Bad Request with details.
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<OrderResponse> handleValidationExceptions(Exception ex) {
        String errorMessage = "Validation failed: ";
        if (ex instanceof MethodArgumentNotValidException manve) {
            errorMessage += manve.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + " " + error.getDefaultMessage())
                    .reduce((m1, m2) -> m1 + "; " + m2)
                    .orElse("Invalid request.");
        } else if (ex instanceof BindException be) {
            errorMessage += be.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + " " + error.getDefaultMessage())
                    .reduce((m1, m2) -> m1 + "; " + m2)
                    .orElse("Invalid request.");
        } else {
            errorMessage += "Invalid request.";
        }
        logger.warn("Validation error: {}", errorMessage);
        return ResponseEntity
                .badRequest()
                .body(OrderResponse.error(errorMessage));
    }
}
