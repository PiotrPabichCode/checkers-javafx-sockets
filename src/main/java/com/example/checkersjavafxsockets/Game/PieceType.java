package com.example.checkersjavafxsockets.Game;

public enum PieceType {
    WHITE(-1), RED(1), WHITE_PROMOTED(-2), RED_PROMOTED(2);

    public final int moveDirection;

    PieceType(int moveDirection){
        this.moveDirection = moveDirection;
    }
}
