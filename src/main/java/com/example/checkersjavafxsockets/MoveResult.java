package com.example.checkersjavafxsockets;

public class MoveResult {

    private MoveType moveType;
    private Piece piece;

    public MoveResult(MoveType moveType, Piece piece){
        this.moveType = moveType;
        this.piece = piece;
    }

    public MoveResult(MoveType moveType){
        this(moveType, null);
    }

    public MoveType getMoveType(){
        return this.moveType;
    }

    public Piece getPiece(){
        return this.piece;
    }
}
