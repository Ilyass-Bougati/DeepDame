package com.deepdame.engine.core.model;

import org.springframework.data.annotation.PersistenceCreator;

import java.util.Arrays;

public class Board {
    public static final int SIZE = 8;
    private final Piece[][] grid;

    public Board() {
        this.grid = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    @PersistenceCreator
    public Board(Piece[][] grid) {
        this.grid = grid;
    }

    private void initializeBoard(){

        for (int row = 0; row < SIZE; row++){
            Arrays.fill(grid[row], null);
        }

        for (int row = 0; row < SIZE; row++){
            for (int col = 0; col < SIZE; col++){
                if((row + col)%2 != 0){
                    if (row < 3){
                        grid[row][col] = Piece.regular(PieceType.WHITE);
                    } else if (row > 4) {
                        grid[row][col] = Piece.regular(PieceType.BLACK);
                    }
                }
            }
        }
    }

    public boolean isValidBounds(Position p) {
        return p.row() >= 0 && p.row() < SIZE && p.col() >= 0 && p.col() < SIZE;
    }


    public Piece getPiece(Position position){
        if (!isValidBounds(position)) return null;
        return grid[position.row()][position.col()];
    }

    public void setPiece(Position position, Piece piece){
        if (isValidBounds(position)){
            grid[position.row()][position.col()] = piece;
        }
    }

    public void removePiece(Position position){
        setPiece(position, null);
    }

    // for debug
    public void printBoard(){
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                Piece p = grid[i][j];
                if (p == null) System.out.print(". ");
                else if (p.type() == PieceType.BLACK) System.out.print(p.isKing() ? "B " : "b ");
                else System.out.print(p.isKing() ? "W " : "w ");
            }
            System.out.println();
        }
    }
}
