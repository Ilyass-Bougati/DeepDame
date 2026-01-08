package com.deepdame.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AiConfig {

    @Bean("aiClient")
    @ConditionalOnProperty(name = "ai.provider", havingValue = "huggingface")
    public RestClient huggingFaceClient(
            @Value("${ai.huggingface.base-url:https://router.huggingface.co/v1}") String baseUrl,
            @Value("${ai.huggingface.api-key}") String apiKey) {

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean("aiClient")
    @ConditionalOnProperty(name = "ai.provider", havingValue = "ollama")
    public RestClient ollamaClient(@Value("${ai.ollama.url:http://localhost:11434/api/generate}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean("aiClient")
    @ConditionalOnProperty(name = "ai.provider", havingValue = "gemini")
    public RestClient geminiClient(@Value("${ai.gemini.url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
