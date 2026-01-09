package com.deepdame.service.ai.provider;

import com.deepdame.engine.core.model.Board;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.AiDifficulty;
import com.deepdame.service.ai.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.provider", havingValue = "huggingface")
public class HuggingFaceBotService implements AiBotService {

    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    @Value("${ai.huggingface.model:meta-llama/Llama-3.2-3B-Instruct}")
    private String modelName;

    @Override
    public Move getAiMove(Board board, List<Move> legalMoves, AiDifficulty difficulty) {
        String systemPrompt = promptBuilder.buildSystemPrompt(difficulty);
        String userPrompt = promptBuilder.buildUserPrompt(board, legalMoves);

        var requestBody = Map.of(
                "model", modelName,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "max_tokens", 500,
                "temperature", 0.1,
                "stream", false
        );

        String response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

        return parseResponse(response, legalMoves);
    }

    private Move parseResponse(String jsonResponse, List<Move> moves) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode choices = root.get("choices");

            if (choices == null || choices.isEmpty()) {
                throw new IllegalStateException("HF response missing 'choices'");
            }

            String content = choices.get(0).get("message").get("content").asText();
            content = cleanMarkdown(content);

            JsonNode aiDecision = objectMapper.readTree(content);
            int index = aiDecision.get("moveIndex").asInt();

            if (index >= 0 && index < moves.size()) {
                return moves.get(index);
            } else {
                throw new IllegalStateException("HF returned invalid move index: " + index);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse HF response", e);
        }
    }

    private String cleanMarkdown(String text) {
        if (text.contains("```json")) {
            return text.replace("```json", "").replace("```", "").trim();
        } else if (text.contains("```")) {
            return text.replace("```", "").trim();
        }
        return text;
    }
}