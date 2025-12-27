package com.deepdame.engine.core.model;

public record Move(Position form, Position to) {

    public boolean isJump(){
        return false;
    }

    // get the coordinate of the piece being jumped over
    public Position getJumpedPosition(){
        return null;
    }
}
