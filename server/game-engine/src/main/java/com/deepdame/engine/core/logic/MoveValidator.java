package com.deepdame.engine.core.logic;

import com.deepdame.engine.core.model.*;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {


    // the main function : cchecks if a specific move is allowed
    public boolean isValidMove(Board board, Move move, PieceType player){

        return true;
    }


    // this fucntion should scan entire board for a specific player and return all the allowd/legal moves
    public List<Move> getLegalMoves(Board board, PieceType player){
        List<Move> jumps = new ArrayList<>();
        List<Move> slides = new ArrayList<>();


        // foreced jump rule
        if (!jumps.isEmpty()) {
            return jumps;
        }
        return slides;
    }
}
