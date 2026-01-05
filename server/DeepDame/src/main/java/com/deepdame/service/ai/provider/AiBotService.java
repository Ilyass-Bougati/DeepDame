package com.deepdame.service.ai.provider;

import com.deepdame.engine.core.model.Board;
import com.deepdame.engine.core.model.Move;
import com.deepdame.enums.AiDifficulty;

import java.util.List;

public interface AiBotService {
    Move getAiMove(Board board, List<Move> legalMoves, AiDifficulty difficulty);
}
