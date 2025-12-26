package com.deepdame.engine.core.model;

public record Piece(PieceType type, boolean isKing) {

    public Piece regular(PieceType type){
        return new Piece(type, false);
    }

    public Piece promote(){
        return new Piece(this.type, true);
    }
}
