package com.pitchplease.facility.discovery.service;

/**
 * Interface for LLM providers
 * This interface defines the contract that all LLM adapters must implement
 */
public interface LLMService {
    /**
     * Generates content using the LLM based on the provided prompt
     * 
     * @param prompt The input text to send to the LLM
     * @return The generated content from the LLM
     */
    String generateContent(String prompt);
}