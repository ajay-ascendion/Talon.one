package com.app.controller;

import com.app.model.CartRequest;
import com.app.model.RewardsResponse;
import com.app.service.RewardsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for evaluating rewards based on cart data.
 * Exposes endpoints under /rewards.
 */
@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardsController {

    private static final Logger logger = LoggerFactory.getLogger(RewardsController.class);

    private final RewardsService rewardsService;

    /**
     * Evaluates rewards for the given cart.
     *
     * @param cartRequest the cart request payload (validated)
     * @return ResponseEntity with RewardsResponse and HTTP status
     */
    @PostMapping("/evaluate")
    public ResponseEntity<RewardsResponse> evaluateRewards(
            @Valid @RequestBody CartRequest cartRequest) {
        RewardsResponse rewardsResponse = rewardsService.evaluateRewards(cartRequest);
        return ResponseEntity.ok(rewardsResponse);
    }

    /**
     * Handles validation errors for @Valid annotated request bodies.
     *
     * @param ex the exception thrown on validation failure
     * @return ResponseEntity with error details and 400 Bad Request
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException manve) {
            manve.getBindingResult().getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        } else if (ex instanceof BindException be) {
            be.getBindingResult().getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        }
        logger.warn("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles all other uncaught exceptions gracefully.
     *
     * @param ex the exception
     * @return ResponseEntity with error message and 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
