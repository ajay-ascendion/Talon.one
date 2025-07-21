package com.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * Configuration class for RestTemplate used to communicate with Talon.One's Integration API.
 * <p>
 * This configuration ensures:
 * <ul>
 *     <li>Singleton, thread-safe RestTemplate instance for TalonOneClient</li>
 *     <li>API key is securely injected from application properties</li>
 *     <li>All requests to Talon.One are logged concisely (method and URI, no sensitive data)</li>
 *     <li>Authorization header is attached to every outgoing request</li>
 * </ul>
 *
 * <p>
 * Required properties in application.properties:
 * <ul>
 *     <li>talonone.api-key=YOUR_TALONONE_API_KEY</li>
 * </ul>
 * </p>
 *
 * @author Your Name
 */
@Configuration
public class RestTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    /**
     * The Talon.One API key, injected from application properties.
     */
    @Value("${talonone.api-key}")
    private String talonOneApiKey;

    /**
     * Defines a singleton, thread-safe RestTemplate bean configured for TalonOneClient.
     * Adds an interceptor for concise logging and API key authentication.
     *
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate talonOneRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new TalonOneApiInterceptor(talonOneApiKey)));
        return restTemplate;
    }

    /**
     * Interceptor for attaching the Talon.One API key and logging request details.
     * Logs HTTP method and URI only (no sensitive data).
     */
    private static class TalonOneApiInterceptor implements ClientHttpRequestInterceptor {

        private final String apiKey;

        /**
         * Constructs the interceptor with the provided API key.
         *
         * @param apiKey the Talon.One API key
         */
        public TalonOneApiInterceptor(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public ClientHttpResponse intercept(
                ClientHttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

            // Attach Authorization header (Bearer token)
            request.getHeaders().setBearerAuth(apiKey);

            // Log HTTP method and URI (no sensitive data)
            logger.info("Talon.One API Request: {} {}", request.getMethod(), request.getURI());

            return execution.execute(request, body);
        }
    }
}
