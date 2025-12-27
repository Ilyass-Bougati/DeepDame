package com.deepdame.engine.core.logic;

import com.deepdame.engine.core.model.*;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {


    // the main function : cchecks if a specific move is allowed
    public boolean isValidMove(Board board, Move move, PieceType player){
        if (move == null) return false;
        List<Move> moveList = getLegalMoves(board, player);

        return moveList.contains(move);
    }


    // this fucntion should scan entire board for a specific player and return all the allowd/legal moves
    public List<Move> getLegalMoves(Board board, PieceType player){
        List<Move> jumps = new ArrayList<>();
        List<Move> slides = new ArrayList<>();


        for(int row = 0; row < board.SIZE; row++){
            for(int col = 0; col < board.SIZE; col++){
                Position pos = new Position(row, col);
                Piece piece = board.getPiece(pos);
                if(piece != null && piece.type().equals(player)){
                    findMovesForPiece(board, pos, piece, jumps, slides);
                }
            }

        }


        // foreced jump rule
        if (!jumps.isEmpty()) {
            return jumps;
        }
        return slides;
    }

    private int[][] getDirections(Piece piece){
        if (piece.isKing()) {
            return new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }
        if (piece.type() == PieceType.WHITE) {
            return new int[][]{{1, -1}, {1, 1}};
        } else {
            return new int[][]{{-1, -1}, {-1, 1}};
        }
    }

    private void findMovesForPiece(Board board, Position pos, Piece piece, List<Move> jumps, List<Move> slides){
        int[][] directions = getDirections(piece);
        for(int[] direction : directions){
            int dRow = direction[0];
            int dCol = direction[1];

            Position next = new Position(pos.row() + dRow, pos.col() + dCol);
            Position landing = new Position(pos.row() + (2 * dRow), pos.col() + (2 * dCol));
            Piece middlePiece = board.getPiece(next);
            Piece landingPiece = board.getPiece(landing);

            if (middlePiece != null && !middlePiece.type().equals(piece.type())) {

                if (board.isValidBounds(landing) && landingPiece == null) {
                    jumps.add(new Move(pos, landing));
                }
            }

            if (board.isValidBounds(next) && board.getPiece(next) == null) {
                slides.add(new Move(pos, next));
            }
        }
    }

}
