package com.ciberspring.api.employee_mgmt.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GooglePlacesService {

    private static final Logger logger = LoggerFactory.getLogger(GooglePlacesService.class);

    @Value("${google.places.api.key:}")
    private String apiKey;

    private final HttpClient httpClient;

    public GooglePlacesService() {
        this.httpClient = HttpClient.newHttpClient();
        validateApiKeyOnStartup();
    }

    /**
     * Validate API key on startup
     */
    private void validateApiKeyOnStartup() {
        logger.info("=== GOOGLE PLACES API KEY VALIDATION ===");
        logger.info("API Key configured: {}", apiKey != null && !apiKey.isEmpty());
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.error("GOOGLE PLACES API KEY IS NOT CONFIGURED!");
            return;
        }
        
        logger.info("Testing API key...");
        boolean isValid = validateApiKey();
        
        if (isValid) {
            logger.info("✅ Google Places API key is VALID and working");
        } else {
            logger.error("❌ Google Places API key is INVALID or has issues");
        }
        logger.info("=== END VALIDATION ===");
    }

    /**
     * Get address predictions from Google Places Autocomplete API
     */
    public String getPlacePredictions(String input) {
        logger.info("Getting place predictions for: '{}'", input);
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            String error = "Google Places API key is not configured. Please contact administrator.";
            logger.error(error);
            return "{\"error\": \"" + error + "\", \"predictions\": []}";
        }

        if (input == null || input.trim().length() < 2) {
            return "{\"predictions\": []}";
        }

        try {
            String encodedInput = URLEncoder.encode(input.trim(), StandardCharsets.UTF_8);
            String url = String.format(
                "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&key=%s",
                encodedInput, apiKey
            );

            logger.debug("Calling Google Places API: {}", url.replace(apiKey, "HIDDEN_KEY"));
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            logger.info("Google Places API Response Status: {}", response.statusCode());
            logger.debug("Google Places API Response Body: {}", response.body());
            
            if (response.statusCode() == 200) {
                // Check if response contains error
                if (response.body().contains("\"status\" : \"REQUEST_DENIED\"")) {
                    logger.error("Google Places API returned REQUEST_DENIED. Check API key permissions.");
                    return "{\"error\": \"API key rejected by Google Places. Please check API key configuration.\", \"predictions\": []}";
                }
                if (response.body().contains("\"status\" : \"OVER_QUERY_LIMIT\"")) {
                    logger.error("Google Places API quota exceeded.");
                    return "{\"error\": \"Google Places API quota exceeded. Please try again later.\", \"predictions\": []}";
                }
                
                logger.info("✅ Successfully fetched predictions for: '{}'", input);
                return response.body();
            } else if (response.statusCode() == 403) {
                logger.error("Google Places API returned 403 Forbidden. API key may be invalid or restricted.");
                return "{\"error\": \"Access forbidden. Please check Google Places API key configuration.\", \"predictions\": []}";
            } else {
                logger.error("Google Places API returned error status: {}", response.statusCode());
                return "{\"error\": \"Google Places API returned error: " + response.statusCode() + "\", \"predictions\": []}";
            }

        } catch (Exception e) {
            logger.error("Error calling Google Places Autocomplete API: {}", e.getMessage(), e);
            return "{\"error\": \"Failed to connect to Google Places service: " + e.getMessage() + "\", \"predictions\": []}";
        }
    }

    /**
     * Get detailed place information from Google Places Details API
     */
    public String getPlaceDetails(String placeId) {
        logger.info("Getting place details for placeId: {}", placeId);
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return "{\"error\": \"Google Places API key is not configured\"}";
        }

        if (placeId == null || placeId.trim().isEmpty()) {
            return "{\"error\": \"Place ID is required\"}";
        }

        try {
            String encodedPlaceId = URLEncoder.encode(placeId.trim(), StandardCharsets.UTF_8);
            String url = String.format(
                "https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&key=%s",
                encodedPlaceId, apiKey
            );

            logger.debug("Calling Google Places Details API: {}", url.replace(apiKey, "HIDDEN_KEY"));
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            logger.info("Google Places Details API Response Status: {}", response.statusCode());
            
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                logger.error("Google Places Details API returned error status: {}", response.statusCode());
                return "{\"error\": \"Google Places Details API returned error: " + response.statusCode() + "\"}";
            }

        } catch (Exception e) {
            logger.error("Error calling Google Places Details API: {}", e.getMessage(), e);
            return "{\"error\": \"Failed to fetch place details: " + e.getMessage() + "\"}";
        }
    }

    /**
     * Validate if the API key is working
     */
    public boolean validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return false;
        }

        try {
            // Test with a simple query
            String testUrl = String.format(
                "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=New+York&key=%s",
                apiKey
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(testUrl))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            boolean isValid = response.statusCode() == 200 && 
                            response.body() != null && 
                            response.body().contains("\"status\" : \"OK\"");
            
            if (!isValid) {
                logger.error("API Key Validation Failed. Response: {}", response.body());
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.warn("Google Places API key validation failed: {}", e.getMessage());
            return false;
        }
    }
}