package com.deepdame.service.ai;

import com.deepdame.engine.core.model.Board;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.AiDifficulty;
import com.deepdame.service.ai.provider.AiBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiOrchestrator {

    private final AiBotService aiBotService;

    public Move getAiMove(Board board, List<Move> legalMoves, AiDifficulty difficulty) {
        if (legalMoves.isEmpty()) return null;

        if (difficulty == AiDifficulty.EASY) {
            Collections.shuffle(legalMoves);
            return legalMoves.get(0);
        }

        try {
            return aiBotService.getAiMove(board, legalMoves, difficulty);
        } catch (Exception e) {
            log.trace("AI Service failed: {}. Falling back to random move.", e.getMessage());
            Collections.shuffle(legalMoves);
            return legalMoves.get(0);
        }
    }
}