package com.app.talonone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.app.model.ProfileDTO;
import com.app.model.SessionDTO;
import com.app.model.RewardsResponse;

/**
 * TalonOneClient is a reusable, centralized client for interacting with Talon.One's Integration API.
 * <p>
 * It provides methods to update customer profiles, evaluate sessions for rewards, and confirm loyalty points.
 * The client manages HTTP communication, authentication headers, and error handling, and is designed for
 * maintainability and testability within a Spring Boot application.
 * <p>
 * Configuration properties required in application.properties:
 * <ul>
 *     <li>talonone.base-url - The base URL of the Talon.One Integration API</li>
 *     <li>talonone.api-key - The API key for authenticating requests</li>
 * </ul>
 *
 * Example usage:
 * <pre>
 *     talonOneClient.updateProfile("user123", profileDto);
 *     RewardsResponse rewards = talonOneClient.evaluateSession(sessionDto);
 *     talonOneClient.confirmLoyalty("user123", 100.0);
 * </pre>
 *
 * @author Your Name
 */
@Component
public class TalonOneClient {

    private static final Logger logger = LoggerFactory.getLogger(TalonOneClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    /**
     * Constructs a new TalonOneClient with injected configuration and RestTemplate.
     *
     * @param restTemplate the RestTemplate used for HTTP communication
     * @param baseUrl the base URL of the Talon.One Integration API
     * @param apiKey the API key for authentication
     */
    public TalonOneClient(
            RestTemplate restTemplate,
            @Value("${talonone.base-url}") String baseUrl,
            @Value("${talonone.api-key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * Updates the customer profile in Talon.One.
     *
     * @param userId the unique identifier of the user
     * @param dto the profile data to update
     * @throws TalonOneClientException if the request fails or Talon.One returns an error
     */
    public void updateProfile(String userId, ProfileDTO dto) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/v1/profiles/{userId}")
                .buildAndExpand(userId)
                .toUriString();

        HttpHeaders headers = buildHeaders();
        HttpEntity<ProfileDTO> entity = new HttpEntity<>(dto, headers);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            logger.info("Successfully updated profile for userId={}", userId);
        } catch (HttpStatusCodeException ex) {
            logger.error("Failed to update profile for userId={}: {}", userId, ex.getResponseBodyAsString(), ex);
            throw new TalonOneClientException("Failed to update profile: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            logger.error("Unexpected error updating profile for userId={}", userId, ex);
            throw new TalonOneClientException("Unexpected error updating profile", ex);
        }
    }

    /**
     * Evaluates a session in Talon.One to determine applicable rewards and discounts.
     *
     * @param dto the session data to evaluate
     * @return the rewards response from Talon.One
     * @throws TalonOneClientException if the request fails or Talon.One returns an error
     */
    public RewardsResponse evaluateSession(SessionDTO dto) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/v1/sessions")
                .toUriString();

        HttpHeaders headers = buildHeaders();
        HttpEntity<SessionDTO> entity = new HttpEntity<>(dto, headers);

        try {
            ResponseEntity<RewardsResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, RewardsResponse.class);
            logger.info("Successfully evaluated session for userId={}", dto.getUserId());
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            logger.error("Failed to evaluate session for userId={}: {}", dto.getUserId(), ex.getResponseBodyAsString(), ex);
            throw new TalonOneClientException("Failed to evaluate session: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            logger.error("Unexpected error evaluating session for userId={}", dto.getUserId(), ex);
            throw new TalonOneClientException("Unexpected error evaluating session", ex);
        }
    }

    /**
     * Confirms a loyalty transaction in Talon.One for the specified user and amount.
     *
     * @param userId the unique identifier of the user
     * @param totalAmount the total amount to confirm for loyalty
     * @throws TalonOneClientException if the request fails or Talon.One returns an error
     */
    public void confirmLoyalty(String userId, double totalAmount) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/v1/loyalty/{userId}/confirm")
                .buildAndExpand(userId)
                .toUriString();

        HttpHeaders headers = buildHeaders();
        // Assuming the API expects a JSON body with "totalAmount"
        String body = String.format("{\"totalAmount\": %.2f}", totalAmount);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            logger.info("Successfully confirmed loyalty for userId={}, amount={}", userId, totalAmount);
        } catch (HttpStatusCodeException ex) {
            logger.error("Failed to confirm loyalty for userId={}: {}", userId, ex.getResponseBodyAsString(), ex);
            throw new TalonOneClientException("Failed to confirm loyalty: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            logger.error("Unexpected error confirming loyalty for userId={}", userId, ex);
            throw new TalonOneClientException("Unexpected error confirming loyalty", ex);
        }
    }

    /**
     * Builds HTTP headers including Content-Type and Authorization.
     *
     * @return the configured HttpHeaders
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return headers;
    }

    /**
     * Exception indicating a problem with Talon.One API communication.
     */
    public static class TalonOneClientException extends RuntimeException {
        public TalonOneClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
