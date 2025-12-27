package com.deepdame.engine.core.logic;

import com.deepdame.engine.core.model.*;

public class GameEngine {

    private final MoveValidator validator;

    public GameEngine() {
        this.validator = new MoveValidator();
    }

    public GameState applyMove(GameState state, Move move){
        Board board = state.getBoard();
        PieceType player = state.getCurrentTurn();

        if (!validator.isValidMove(board, move, player)){
            throw new IllegalArgumentException("Invalide move");
        }

        Piece piece =  board.getPiece(move.from());
        board.removePiece(move.from());
        board.setPiece(move.to(), piece);

        if (move.isJump()){
            Position jumpedPos = move.getJumpedPosition();
            board.removePiece(jumpedPos);
        }

        //check piece promotion
        // check is the game over
        //switch turns
        return state;
    }
}
