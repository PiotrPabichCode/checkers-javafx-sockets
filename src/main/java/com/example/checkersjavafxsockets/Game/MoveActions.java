package com.example.checkersjavafxsockets.Game;

import com.example.checkersjavafxsockets.Checkers;
import com.example.checkersjavafxsockets.UI.Piece;
import com.example.checkersjavafxsockets.UI.PieceType;
import javafx.application.Platform;

import static com.example.checkersjavafxsockets.Game.Creator.convertPixToCoord;
import static com.example.checkersjavafxsockets.Game.Creator.createTerminalMessage;

public class MoveActions {

    private Checkers checkers;

    public MoveActions(Checkers checkers){
        this.checkers = checkers;
    }

    public void requestMove(Piece piece, int newX, int newY){
        if(!checkers.turn){
            MovePiece movePiece = tryMove(piece, newX, newY);
            makeMove(newX, newY, piece, movePiece);
            checkWin();
            return;
        }
//        sendMessage(createTerminalMessage(newX, newY, piece, new MovePiece(MoveType.NONE)));
    }

    public MovePiece tryMove(Piece piece, int newX, int newY){
        if(!validPieceType(piece)){
            return new MovePiece(MoveType.NONE);
        }
        MovePiece movePiece = new MovePiece(MoveType.NONE);
        if(checkers.board[newX][newY].hasPiece() || (newX + newY) % 2 == 0){
            return movePiece;
        }
        int oldX = convertPixToCoord(piece.getOldX());
        int oldY = convertPixToCoord(piece.getOldY());
        if(tryNormalMove(piece, oldX, oldY, newX, newY, movePiece)){
            return movePiece;
        }
        tryKillMove(piece, oldX, oldY, newX, newY, movePiece);
        return movePiece;
    }

    public void makeMove(int newX, int newY, Piece piece, MovePiece movePiece){
        MoveType moveType = movePiece.getMoveType();
        switch (moveType){
            case NONE:
                piece.abortMove();
                break;
            case NORMAL:
                checkers.board[convertPixToCoord(piece.getOldX())][convertPixToCoord(piece.getOldY())].setPiece(null);
                piece.move(newX, newY);
                checkers.board[newX][newY].setPiece(piece);
                checkers.setTurn(false);
                if((newY == 0 && piece.getType() == PieceType.WHITE) || (newY == 7 && piece.getType() == PieceType.RED)){
                    Platform.runLater(piece::promotePiece);
                }
                break;
            case KILL:
                checkers.board[convertPixToCoord(piece.getOldX())][convertPixToCoord(piece.getOldY())].setPiece(null);
                piece.move(newX, newY);
                checkers.board[newX][newY].setPiece(piece);
                checkers.setTurn(false);

                Piece otherPiece = movePiece.getPiece();
                checkers.board[convertPixToCoord(otherPiece.getOldX())][convertPixToCoord(otherPiece.getOldY())].setPiece(null);
                Platform.runLater(() -> checkers.removePiece(otherPiece));
                if(piece.getType() == PieceType.RED || piece.getType() == PieceType.RED_PROMOTED){
                    checkers.setWhitePieces();
                } else{
                    checkers.setRedPieces();
                }
                if((newY == 0 && piece.getType() == PieceType.WHITE) || (newY == 7 && piece.getType() == PieceType.RED)){
                    Platform.runLater(piece::promotePiece);
                }
                break;
        }
    }

    public void checkWin(){
        if(checkers.getWhitePieces() == 0 || checkers.getRedPieces() == 0) {
            checkers.setWinner();
        }
    }

    public boolean validPieceType(Piece piece){
        if((piece.getType() == PieceType.WHITE || piece.getType() == PieceType.WHITE_PROMOTED) && checkers.getDirection() == MoveType.WHITENOW){
            checkers.setDirection(MoveType.REDNOW);
            return true;
        }
        if((piece.getType() == PieceType.RED || piece.getType() == PieceType.RED_PROMOTED) && checkers.getDirection() == MoveType.REDNOW){
            checkers.setDirection(MoveType.WHITENOW);
            return true;
        }
        return false;
    }

    public boolean tryNormalMove(Piece piece, int oldX, int oldY, int newX, int newY, MovePiece movePiece){
        if (Math.abs(newX - oldX) == 1 && newY - oldY == piece.getType().moveDirection) {
            movePiece.setMoveType(MoveType.NORMAL);
            return true;
        }
        if((piece.getType() == PieceType.RED_PROMOTED || piece.getType() == PieceType.WHITE_PROMOTED) && (Math.abs(newX - oldX) == 1 && Math.abs(newY - oldY) == 1)){
            movePiece.setMoveType(MoveType.NORMAL);
            return true;
        }
        return false;
    }
    public boolean tryKillMove(Piece piece, int oldX, int oldY, int newX, int newY, MovePiece movePiece){
        if(Math.abs(newX - oldX) == 2 && Math.abs(newY - oldY) == 2){
            int middleX = oldX + (newX - oldX) / 2;
            int middleY = oldY + (newY - oldY) / 2;
            if(checkers.board[middleX][middleY].hasPiece() && checkers.board[middleX][middleY].getPiece().getType() != piece.getType()) {
                movePiece.setMoveType(MoveType.KILL);
                movePiece.setPiece(checkers.board[middleX][middleY].getPiece());
                return true;
            }
        }
        return false;
    }
}
