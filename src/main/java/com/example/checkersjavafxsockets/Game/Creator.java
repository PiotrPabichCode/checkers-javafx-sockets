package com.example.checkersjavafxsockets.Game;

import com.example.checkersjavafxsockets.Checkers;
import com.example.checkersjavafxsockets.UI.Piece;

import java.util.Random;

public class Creator {

    public Creator(){}
    public static String createTerminalMessage(int newX, int newY, Piece piece, MovePiece movePiece){
        // format: [oldX oldY newX newY Action killX killY]
        String message = String.format("%d %d %d %d %s", convertPixToCoord(piece.getOldX()), convertPixToCoord(piece.getOldY()),
                newX, newY, movePiece.getMoveType().toString());
        if(movePiece.getMoveType() == MoveType.KILL){
            message += String.format(" %d %d", convertPixToCoord(movePiece.getPiece().getOldX()), convertPixToCoord(movePiece.getPiece().getOldY()));
        }
        return message;
    }

    public static int convertPixToCoord(double pixel){
        return (int)((pixel + Checkers.TILE_SIZE / 2) / Checkers.TILE_SIZE);
    }

    public static String generateBotMove(){
        Random random = new Random();
        return random.nextInt(8) + " " + random.nextInt(8) + " " + random.nextInt(8) + " " + random.nextInt(8);
    }
}
