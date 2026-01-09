package com.deepdame.service.ai;

import com.deepdame.engine.core.model.Board;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.AiDifficulty;
import com.deepdame.service.ai.provider.AiBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiOrchestrator {

    private final AiBotService aiBotService;

    @Value("${ai.min-think-time-ms:0}")
    private long minThinkTimeMs;

    public Move getAiMove(Board board, List<Move> legalMoves, AiDifficulty difficulty) {
        if (legalMoves.isEmpty()) return null;

        if (difficulty == AiDifficulty.EASY) {
            simulateThinking();
            return getRandomMove(legalMoves);
        }

        long startTime = System.currentTimeMillis();
        Move selectedMove;

        try {
            selectedMove = aiBotService.getAiMove(board, legalMoves, difficulty);

            if (selectedMove == null) {
                throw new IllegalStateException("AI Provider returned null");
            }

        } catch (Exception e) {
            log.warn("AI Generation failed [Reason: {}]. Fallback to random move.", e.getMessage());
            selectedMove = getRandomMove(legalMoves);
        }

        ensureMinimumDelay(startTime);

        return selectedMove;
    }

    private Move getRandomMove(List<Move> moves) {
        return moves.get(ThreadLocalRandom.current().nextInt(moves.size()));
    }

    private void ensureMinimumDelay(long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed < minThinkTimeMs) {
            try {
                Thread.sleep(minThinkTimeMs - elapsed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void simulateThinking() {
        try {
            Thread.sleep(minThinkTimeMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}