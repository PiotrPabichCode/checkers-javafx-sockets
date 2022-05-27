package com.example.checkersjavafxsockets;

import com.example.checkersjavafxsockets.Game.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Checkers extends Application {

    public static final int TILE_SIZE = 100;
    public static final int MAX_SIZE = 8;

    private Tile[][] board = new Tile[MAX_SIZE][MAX_SIZE];

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private int player;
    private Label label = new Label();
    private Timer timer = new Timer();
    private int whitePieces = 0;
    private int redPieces = 0;
    private float time = 0;

    private boolean myTurn = false;

    private Piece makePiece(PieceType type, int x, int y){
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());
            MovePiece movePiece = tryMove(piece, newX, newY);
            makeMove(newX, newY, piece, movePiece);

        });

        return piece;
    }

    private void makeMove(int newX, int newY, Piece piece, MovePiece movePiece){
        MoveType moveType = movePiece.getMoveType();
        switch (moveType){
            case NONE:
                piece.abortMove();
                break;
            case NORMAL:
                board[toBoard(piece.getOldX())][toBoard(piece.getOldY())].setPiece(null);
                piece.move(newX, newY);
                board[newX][newY].setPiece(piece);
                myTurn = false;
                if((newY == 0 && piece.getType() == PieceType.RED) || (newY == 7 && piece.getType() == PieceType.WHITE)){
                    piece.promotePiece();
                }
                break;
            case KILL:
                board[toBoard(piece.getOldX())][toBoard(piece.getOldY())].setPiece(null);
                piece.move(newX, newY);
                board[newX][newY].setPiece(piece);

                Piece otherPiece = movePiece.getPiece();
                board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                pieceGroup.getChildren().remove(otherPiece);
                myTurn = false;
                if(piece.getType() == PieceType.RED || piece.getType() == PieceType.RED_PROMOTED){
                    whitePieces--;
                } else{
                    redPieces--;
                }
                if((newY == 0 && piece.getType() == PieceType.RED) || (newY == 7 && piece.getType() == PieceType.WHITE)){
                    piece.promotePiece();
                }
                break;
        }
    }

    private Parent createSceneContent(){
        Pane root = new Pane();
        root.setPrefSize(MAX_SIZE * TILE_SIZE, MAX_SIZE * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup, label, timer);

        for(int y = 0; y < MAX_SIZE; y++){
            for(int x = 0; x < MAX_SIZE; x++){
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;
                if(y < MAX_SIZE / 2 - 1 && (x + y) % 2 != 0){
                    piece = makePiece(PieceType.RED, x, y);
                    redPieces++;
                }
                if(y >= MAX_SIZE / 2 + 1 && (x + y) % 2 != 0){
                    piece = makePiece(PieceType.WHITE, x, y);
                    whitePieces++;
                }

                if(piece != null){
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        return root;
    }

    private MovePiece tryMove(Piece piece, int newX, int newY){
        MovePiece movePiece = new MovePiece(MoveType.NONE);
        if(board[newX][newY].hasPiece() || (newX + newY) % 2 == 0){
            return movePiece;
        }
        int oldX = toBoard(piece.getOldX());
        int oldY = toBoard(piece.getOldY());
        if(tryNormalMove(piece, oldX, oldY, newX, newY, movePiece)){
            return movePiece;
        }
        tryKillMove(piece, oldX, oldY, newX, newY, movePiece);
        return movePiece;
    }

    private boolean tryNormalMove(Piece piece, int oldX, int oldY, int newX, int newY, MovePiece movePiece){
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
    private boolean tryKillMove(Piece piece, int oldX, int oldY, int newX, int newY, MovePiece movePiece){
        if(Math.abs(newX - oldX) == 2 && Math.abs(newY - oldY) == 2){
            int middleX = oldX + (newX - oldX) / 2;
            int middleY = oldY + (newY - oldY) / 2;
            if(board[middleX][middleY].hasPiece() && board[middleX][middleY].getPiece().getType() != piece.getType()) {
                movePiece.setMoveType(MoveType.KILL);
                movePiece.setPiece(board[middleX][middleY].getPiece());
                return true;
            }
        }
        return false;
    }

    public void timeCount(){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if(!myTurn){
                time += 0.1;
                Platform.runLater(() -> timer.set("Timer: " + (int)time + "s."));
            }
        }, 0,100, TimeUnit.MILLISECONDS);
    }

    private int toBoard(double pixel){
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(createSceneContent());
        stage.setTitle("Checkers");
        timeCount();
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}