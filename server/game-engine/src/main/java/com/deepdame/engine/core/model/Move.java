package com.deepdame.engine.core.model;

public record Move(Position from, Position to) {

    public boolean isJump(){
        return Math.abs(from.row() - to.row()) == 2;
    }

    // get the coordinate of the piece being jumped over
    public Position getJumpedPosition(){
        if (!isJump()) return null;
        int midRow = (from.row() + to.row()) / 2;
        int midCol = (from.col() + to.col()) / 2;
        return new Position(midRow, midCol);
    }
}
