package com.deepdame.engine.core.model;

import org.springframework.data.annotation.PersistenceCreator;

import java.util.UUID;

public class GameState {

    private UUID id;
    private Board board;
    private PieceType currentTurn;
    private boolean isGameOver;
    private PieceType winner;

    public GameState(UUID id){
        this.id = id;
        this.board = new Board();
        this.currentTurn = PieceType.BLACK;
        this.isGameOver = false;
        this.winner = null;
    }

    @PersistenceCreator
    public GameState(UUID id, Board board, PieceType currentTurn, boolean isGameOver, PieceType winner) {
        this.id = id;
        this.board = board;
        this.currentTurn = currentTurn;
        this.isGameOver = isGameOver;
        this.winner = winner;
    }

    public GameState(){}

    public void switchTurn(){
        if (isGameOver) return;
        this.currentTurn = (this.currentTurn == PieceType.BLACK) ? PieceType.WHITE : PieceType.BLACK;
    }

    public void finishGame(PieceType winner){
        this.isGameOver = true;
        this.winner = winner;
    }

    public UUID getId() { return id; }
    public Board getBoard() { return board; }
    public PieceType getCurrentTurn() { return currentTurn; }
    public boolean isGameOver() { return isGameOver; }
    public PieceType getWinner() { return winner; }
}
