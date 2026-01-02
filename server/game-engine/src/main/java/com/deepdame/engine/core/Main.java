package com.deepdame.engine.core;

import com.deepdame.engine.core.logic.*;
import com.deepdame.engine.core.model.*;

import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args){
        GameEngine engine = new GameEngine();
        GameState state = new GameState(UUID.randomUUID());
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== CHECKERS ENGINE TEST ===");
        System.out.println("Format moves as: 'r1 c1 r2 c2' (e.g., '5 0 4 1')");

        while (!state.isGameOver()) {
            printBoard(state.getBoard());
            System.out.println("Turn: " + state.getCurrentTurn());

            if (state.getCurrentTurn() == PieceType.BLACK) {
                System.out.print("Enter Move: ");
                try {
                    int r1 = scanner.nextInt();
                    int c1 = scanner.nextInt();
                    int r2 = scanner.nextInt();
                    int c2 = scanner.nextInt();

                    Move move = new Move(new Position(r1, c1), new Position(r2, c2));
                    state = engine.applyMove(state, move);

                } catch (Exception e) {
                    System.out.println("INVALID MOVE: " + e.getMessage());
                    scanner.nextLine();
                }
            } else {
                System.out.println("AI is thinking...");
                var legalMoves = new MoveValidator().getLegalMoves(state.getBoard(), PieceType.WHITE);

                if (legalMoves.isEmpty()) {
                    System.out.println("AI has no moves! Red Wins!");
                    break;
                }

                Move aiMove = legalMoves.get(0);
                System.out.println("AI Moved: " + aiMove);
                state = engine.applyMove(state, aiMove);
            }
        }
    }

    private static void printBoard(Board board) {
        System.out.println("    0 1 2 3 4 5 6 7");
        for (int r = 0; r < 8; r++) {
            System.out.print(r + " |");
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(new Position(r, c));
                if (p == null) {
                    System.out.print(" .");
                } else {
                    String symbol = (p.type() == PieceType.BLACK) ? "b" : "w";
                    if (p.isKing()) symbol = symbol.toUpperCase();
                    System.out.print(" " + symbol);
                }
            }
            System.out.println();
        }
    }
}
