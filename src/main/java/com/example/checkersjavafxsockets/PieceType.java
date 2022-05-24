package com.example.checkersjavafxsockets;

public enum PieceType {
    WHITE(-1), RED(1);

    final int moveDirection;

    PieceType(int moveDirection){
        this.moveDirection = moveDirection;
    }
}
