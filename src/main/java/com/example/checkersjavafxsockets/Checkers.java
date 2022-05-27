package com.example.checkersjavafxsockets;

import com.example.checkersjavafxsockets.Game.*;
import com.example.checkersjavafxsockets.Server.ServerMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
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
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    MoveType direction = MoveType.WHITENOW;
    private static String playMode = null;

    private boolean move = false;

    public Piece makePiece(PieceType type, int x, int y){
        Piece piece = new Piece(type, x, y);
        move = false;
        piece.setOnMouseReleased(e -> {
            int newX = convertPixToCoord(piece.getLayoutX());
            int newY = convertPixToCoord(piece.getLayoutY());
            MovePiece movePiece = tryMove(piece, newX, newY);
            makeMove(newX, newY, piece, movePiece);
        });

        return piece;
    }
    private boolean validMove(Piece piece){
        if((piece.getType() == PieceType.WHITE || piece.getType() == PieceType.WHITE_PROMOTED) && direction == MoveType.WHITENOW){
            direction = MoveType.REDNOW;
            return true;
        }
        if((piece.getType() == PieceType.RED || piece.getType() == PieceType.RED_PROMOTED) && direction == MoveType.REDNOW){
            direction = MoveType.WHITENOW;
            return true;
        }
        return false;
    }

    public void makeMove(int newX, int newY, Piece piece, MovePiece movePiece){
        MoveType moveType = movePiece.getMoveType();
        switch (moveType){
            case NONE:
                piece.abortMove();
                break;
            case NORMAL:
                board[convertPixToCoord(piece.getOldX())][convertPixToCoord(piece.getOldY())].setPiece(null);
                piece.move(newX, newY);
                board[newX][newY].setPiece(piece);
                if((newY == 0 && piece.getType() == PieceType.WHITE) || (newY == 7 && piece.getType() == PieceType.RED)){
                    piece.promotePiece();
                }
                break;
            case KILL:
                board[convertPixToCoord(piece.getOldX())][convertPixToCoord(piece.getOldY())].setPiece(null);
                piece.move(newX, newY);
                board[newX][newY].setPiece(piece);

                Piece otherPiece = movePiece.getPiece();
                board[convertPixToCoord(otherPiece.getOldX())][convertPixToCoord(otherPiece.getOldY())].setPiece(null);
                pieceGroup.getChildren().remove(otherPiece);
                if(piece.getType() == PieceType.RED || piece.getType() == PieceType.RED_PROMOTED){
                    whitePieces--;
                } else{
                    redPieces--;
                }
                if((newY == 0 && piece.getType() == PieceType.WHITE) || (newY == 7 && piece.getType() == PieceType.RED)){
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
        if(!validMove(piece)){
            return new MovePiece(MoveType.NONE);
        }
        MovePiece movePiece = new MovePiece(MoveType.NONE);
        if(board[newX][newY].hasPiece() || (newX + newY) % 2 == 0){
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
            Platform.runLater(() -> timer.set("Playtime: " + String.format("%.1f", time) + "s."));
            time += 0.1;
        }, 0,100, TimeUnit.MILLISECONDS);
    }

    private int convertPixToCoord(double pixel){
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }
    private void listenServer(){
        new Thread(() -> {
            while(socket.isConnected()){

            }
        }).start();
    }

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        try{
            socket = new Socket("localhost", ServerMain.PORT);
        } catch (IOException e){
            Thread.sleep(3000);
            socket = new Socket("localhost", ServerMain.PORT);
        }
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            bufferedWriter.write(playMode);

        } catch (IOException e){
            closeAll();
        }
        timeCount();
        listenServer();
        Scene scene = new Scene(createSceneContent());
        stage.setTitle("Checkers");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
//        playMode = args[0];
        launch(args);
    }

    public void closeAll() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}