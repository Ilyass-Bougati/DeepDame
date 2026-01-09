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

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ai.provider", havingValue = "gemini")
public class GeminiBotService implements AiBotService {

    private final PromptBuilder promptBuilder;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    @Override
    public Move getAiMove(Board board, List<Move> legalMoves, AiDifficulty difficulty) {

        String prompt = promptBuilder.buildSystemPrompt(difficulty) + "\n"
                + promptBuilder.buildUserPrompt(board, legalMoves);

        var requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        String response;
        try {
            response = restClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

        } catch (Exception e) {
            return legalMoves.get(0);
        }

        return parseResponse(response, legalMoves);
    }

    private Move parseResponse(String jsonResponse, List<Move> moves) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            JsonNode aiDecision = objectMapper.readTree(text);
            int index = aiDecision.get("moveIndex").asInt();

            if (index >= 0 && index < moves.size()) {
                return moves.get(index);
            }
        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
        }
        return moves.get(0);
    }
}