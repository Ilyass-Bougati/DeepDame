package com.deepdame.service.ai;

import com.deepdame.engine.core.model.Board;
import com.deepdame.engine.core.model.Move;
import com.deepdame.engine.core.model.Piece;
import com.deepdame.engine.core.model.PieceType;
import com.deepdame.enums.AiDifficulty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    public String buildSystemPrompt(AiDifficulty difficulty) {
        String base = """
            You are a Grandmaster Checkers (Draughts) Engine playing as WHITE.
            Your goal is to defeat the opponent (BLACK).
            
            RULES:
            1. You will receive a visual representation of the board.
            2. You will receive a numbered list of LEGAL MOVES.
            3. You MUST pick exactly one move from the list by its ID.
            4. Return ONLY a valid JSON object: {"moveIndex": <number>, "reasoning": "<short text>"}
            """;

        String strategy = switch (difficulty) {
            case MEDIUM -> "STRATEGY: Play conservatively. Avoid leaving your pieces exposed to obvious captures. Do not think too deep, just safe play.";
            case HARD -> "STRATEGY: Play aggressively. Calculate trades to your advantage. Prioritize King safety and controlling the center. Try to trap the opponent.";
            default -> "";
        };

        return base + "\n" + strategy;
    }

    public String buildUserPrompt(Board board, List<Move> legalMoves) {
        StringBuilder sb = new StringBuilder();

        sb.append("CURRENT BOARD STATE (b=Black, w=White, B=Black King, W=White King, . = Empty):\n");
        sb.append(renderBoard(board));
        sb.append("\n\n");

        sb.append("AVAILABLE LEGAL MOVES:\n");
        for (int i = 0; i < legalMoves.size(); i++) {
            Move m = legalMoves.get(i);
            sb.append(String.format("ID %d: From (%d,%d) To (%d,%d)\n",
                    i, m.from().row(), m.from().col(), m.to().row(), m.to().col()));
        }

        sb.append("\nWhich ID is the best move? Respond in JSON.");
        return sb.toString();
    }

    private String renderBoard(Board board) {
        StringBuilder sb = new StringBuilder();
        sb.append("  0 1 2 3 4 5 6 7\n");
        for (int r = 0; r < 8; r++) {
            sb.append(r).append(" ");
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(new com.deepdame.engine.core.model.Position(r, c));
                if (p == null) sb.append(". ");
                else if (p.type() == PieceType.BLACK) sb.append(p.isKing() ? "B " : "b ");
                else sb.append(p.isKing() ? "W " : "w ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}