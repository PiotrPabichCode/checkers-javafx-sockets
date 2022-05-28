package com.example.checkersjavafxsockets.Game;

import com.example.checkersjavafxsockets.UI.Piece;

public class MovePiece {

    private MoveType moveType;
    private Piece piece;

    public MovePiece(MoveType moveType, Piece piece){
        this.moveType = moveType;
        this.piece = piece;
    }

    public MovePiece(MoveType moveType){
        this(moveType, null);
    }

    public MoveType getMoveType(){
        return this.moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece(){
        return this.piece;
    }
}
