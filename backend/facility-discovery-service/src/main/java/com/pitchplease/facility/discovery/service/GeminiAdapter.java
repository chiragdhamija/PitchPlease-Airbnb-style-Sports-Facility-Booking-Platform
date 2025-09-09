package com.pitchplease.facility.discovery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class GeminiAdapter implements LLMService {
    
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private String apiKey;
    private String apiEndpoint;
    
    /**
     * Default constructor with default values
     * Note: This is not recommended for production use as it hardcodes API keys
     */
    public GeminiAdapter() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        // You should ideally read these from configuration, but for quick fix:
        this.apiKey = ""; // Replace with actual key
        this.apiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    }
    
    /**
     * Preferred constructor with dependency injection
     */
    public GeminiAdapter(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.endpoint:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}") String apiEndpoint
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiEndpoint = apiEndpoint;
    }
    
    /**
     * Implements the LLMService interface to generate content using Gemini
     * 
     * @param prompt Text prompt to send to Gemini
     * @return Generated response from Gemini
     */
    @Override
    public String generateContent(String prompt) {
        try {
            // Prepare URL with API key
            String url = apiEndpoint + "?key=" + apiKey;
            
            // Prepare request body according to Gemini API format
            ObjectNode rootNode = objectMapper.createObjectNode();
            ArrayNode contentsArray = rootNode.putArray("contents");
            ObjectNode contentNode = contentsArray.addObject();
            ArrayNode partsArray = contentNode.putArray("parts");
            ObjectNode textPart = partsArray.addObject();
            textPart.put("text", prompt);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create HTTP entity
            HttpEntity<String> request = new HttpEntity<>(rootNode.toString(), headers);
            
            // Make API call
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            // Parse response
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            return extractTextFromResponse(responseJson);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate content from Gemini API", e);
        }
    }
    
    /**
     * Helper method to extract text content from Gemini API response
     * 
     * @param responseJson The JSON response from Gemini API
     * @return Extracted text content
     */
    private String extractTextFromResponse(JsonNode responseJson) {
        try {
            // Navigate through response to get to the text content
            JsonNode candidates = responseJson.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).get("content");
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        return parts.get(0).get("text").asText();
                    }
                }
            }
            return "No text content found in response";
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from Gemini API response", e);
        }
    }
}