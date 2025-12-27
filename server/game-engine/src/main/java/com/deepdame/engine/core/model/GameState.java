package com.deepdame.engine.core.model;

public class GameState {

    private final String id;
    private final Board board;
    private PieceType currentTurn;
    private boolean isGameOver;
    private PieceType winner;

    public GameState(String id){
        this.id = id;
        this.board = new Board();
        this.currentTurn = PieceType.BLACK;
        this.isGameOver = false;
        this.winner = null;
    }

    public void switchTurn(){
        if (isGameOver) return;
        this.currentTurn = (this.currentTurn == PieceType.BLACK) ? PieceType.WHITE : PieceType.BLACK;
    }

    public void finishGame(PieceType winner){
        this.isGameOver = true;
        this.winner = winner;
    }

    public String getId() { return id; }
    public Board getBoard() { return board; }
    public PieceType getCurrentTurn() { return currentTurn; }
    public boolean isGameOver() { return isGameOver; }
    public PieceType getWinner() { return winner; }
}
