package com.deepdame.service.ai.provider;

import com.deepdame.engine.core.model.Board;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.AiDifficulty;
import com.deepdame.service.ai.PromptBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.provider", havingValue = "ollama")
public class OllamaBotService implements AiBotService {

    private final PromptBuilder promptBuilder;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.ollama.model}")
    private String modelName;

    @Override
    public Move getAiMove(Board board, List<Move> legalMoves, AiDifficulty difficulty) {
        String systemPrompt = promptBuilder.buildSystemPrompt(difficulty);
        String userPrompt = promptBuilder.buildUserPrompt(board, legalMoves);

        var requestBody = Map.of(
                "model", modelName,
                "prompt", systemPrompt + "\n\n" + userPrompt,
                "stream", false,
                "format", "json"
        );

        String response = restClient.post()
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return parseResponse(response, legalMoves);
    }

    private Move parseResponse(String jsonResponse, List<Move> moves) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            String innerJson = root.get("response").asText();

            JsonNode aiDecision = objectMapper.readTree(innerJson);
            int index = aiDecision.get("moveIndex").asInt();

            if (index >= 0 && index < moves.size()) {
                log.trace("Ollama selected move ID: {}", index);
                return moves.get(index);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse AI response: " + e.getMessage());
        }
        throw new IllegalStateException("AI returned invalid move index");
    }
}