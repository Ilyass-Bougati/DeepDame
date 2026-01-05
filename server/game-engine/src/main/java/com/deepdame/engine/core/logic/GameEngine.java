package com.deepdame.engine.core.logic;

import com.deepdame.engine.core.model.*;

import java.util.List;

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

        checkPromotion(board, piece, move.to());
        checkGameOver(state);

        if (!state.isGameOver()){
            state.switchTurn();
        }

        return state;
    }

    public List<Move> getLegalMoves(Board board, PieceType player){
        return validator.getLegalMoves(board, player);
    }

    private void checkPromotion(Board board ,Piece piece, Position position){
        if (piece.type() == PieceType.BLACK && position.row() == 0){
            Piece kingPiece = board.getPiece(position).promote();
            board.setPiece(position, kingPiece);
        }else if (piece.type() == PieceType.WHITE && position.row() == 7){
            Piece kingPiece = board.getPiece(position).promote();
            board.setPiece(position, kingPiece);
        }
    }

    private void checkGameOver(GameState state){
        PieceType nextTurn = (state.getCurrentTurn() == PieceType.BLACK) ? PieceType.WHITE : PieceType.BLACK;
        List<Move> nextMoves = validator.getLegalMoves(state.getBoard(), nextTurn);

        if (nextMoves.isEmpty()){
            state.finishGame(state.getCurrentTurn());
        }
    }
}
