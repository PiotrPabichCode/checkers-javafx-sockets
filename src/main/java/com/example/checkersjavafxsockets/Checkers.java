package com.example.checkersjavafxsockets;

import com.example.checkersjavafxsockets.Game.*;
import com.example.checkersjavafxsockets.Server.ServerMain;
import com.example.checkersjavafxsockets.UI.Piece;
import com.example.checkersjavafxsockets.UI.PieceType;
import com.example.checkersjavafxsockets.UI.Tile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.checkersjavafxsockets.Game.Creator.convertPixToCoord;

public class Checkers extends Application {

    public static final int TILE_SIZE = 100;
    public static final int MAX_SIZE = 8;

    private final Tile[][] board = new Tile[MAX_SIZE][MAX_SIZE];
    private final Timer timer = new Timer();
    private static String mode = null;
    private int winner = 0;
    private float time = 0;
    private Socket socket;
    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private int player;
    private boolean turn = false;
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        try{
            socket = new Socket("localhost", ServerMain.PORT);
        } catch (IOException e){
            Thread.sleep(5000);
            socket = new Socket("localhost", ServerMain.PORT);
        }
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            sendMessage(mode);
            if(bufferedReader.readLine().equals("1")){
                player = 1;
            } else{
                player = 2;
            }
        } catch (IOException e){
            closeAll();
        }
        timeCount();
        listenServer();
        Scene scene = new Scene(createSceneContent());
        stage.setTitle("Checkers");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        mode = args[0];
        launch();
    }
    private void listenServer(){
        new Thread(() -> {
            String message;
            while(socket.isConnected() && winner == 0) {
                try {
                    message = bufferedReader.readLine();
                    System.out.println(message);
                    if(message.startsWith("TURN")){
                        turn = true;
                    }
                    else{
                        String[] data = message.split(" ");
                        Coordinates oldCoord = new Coordinates(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                        Coordinates newCoord = new Coordinates(Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                        Piece piece = getBoardTile(oldCoord).getPiece();
                        String moveType = data[4];

                        switch (moveType) {
                            case "NONE":
                                makeMove(newCoord, piece, new MovePiece(MoveType.NONE));
                                break;
                            case "NORMAL":
                                makeMove(newCoord, piece, new MovePiece(MoveType.NORMAL));
                                break;
                            case "KILL":
                                Coordinates killCoord = new Coordinates(Integer.parseInt(data[5]), Integer.parseInt(data[6]));
                                Piece pieceDestroyed = getBoardTile(killCoord).getPiece();
                                makeMove(newCoord, piece, new MovePiece(MoveType.KILL, pieceDestroyed));
                                break;
                            case "WINNER_RED":
                                winner = 1;
                                break;
                            case "WINNER_WHITE":
                                winner = 2;
                                break;
                        }
                    }
                } catch (IOException e) {
                    closeAll();
                }
            }
            closeAll();
        }).start();
    }
    public void sendMessage(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeAll();
        }
    }
    public Parent createSceneContent(){
        Pane root = new Pane();
        root.setPrefSize(MAX_SIZE * TILE_SIZE, MAX_SIZE * TILE_SIZE);

        for(int y = 0; y < MAX_SIZE; y++){
            for(int x = 0; x < MAX_SIZE; x++){
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;
                if(y < 3 && (x + y) % 2 != 0){
                    piece = makePiece(PieceType.RED, x, y);
                }
                if(y >= 5 && (x + y) % 2 != 0){
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if(piece != null){
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        root.getChildren().addAll(tileGroup, pieceGroup, timer);

        return root;
    }

    public Piece makePiece(PieceType type, int x, int y){
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e -> {
            Coordinates newCoord = new Coordinates(convertPixToCoord(piece.getLayoutX()), convertPixToCoord(piece.getLayoutY()));
            requestMove(piece, newCoord);
        });

        return piece;
    }

    public void timeCount(){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if(winner != 0){
                time = 5;
                executorService.scheduleAtFixedRate(() -> {
                    if(time <= 0.1){
                        System.exit(1);
                    }
                    Platform.runLater(() -> timer.setTime(((winner == player) ? "You won!\nExit in " : "You lost!\nExit in ") + String.format("%.1f", time)));
                    time -= 0.1;
                }, 0, 100, TimeUnit.MILLISECONDS);
            }
            if(turn){
                Platform.runLater(() -> timer.setTime("Playtime: " + String.format("%.1f", time) + "s.\n" + ((player == 1) ? "Red pieces" : "White pieces")));
                time += 0.1;
            }
        }, 0,100, TimeUnit.MILLISECONDS);
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

    public void requestMove(Piece piece, Coordinates newCoord){
        if(!turn){
            makeMove(newCoord, piece, new MovePiece(MoveType.NONE));
            return;
        }
        sendMessage(convertPixToCoord(piece.getOldX()) + " " + convertPixToCoord(piece.getOldY()) + " " + newCoord.getX() + " " + newCoord.getY() + " " + (player == 1 ? "RED" : "WHITE"));
    }

    public void makeMove(Coordinates newCoord, Piece piece, MovePiece movePiece){
        MoveType moveType = movePiece.getMoveType();
        switch (moveType){
            case NONE:
                piece.abortMove();
                break;
            case NORMAL:
                getBoardTileConverted(piece.getOldCoord()).setPiece(null);
                piece.move(newCoord);
                getBoardTile(newCoord).setPiece(piece);
                turn = false;
                if((newCoord.getY() == 0 && piece.getType() == PieceType.WHITE) || (newCoord.getY() == 7 && piece.getType() == PieceType.RED)){
                    Platform.runLater(piece::promotePiece);
                }
                break;
            case KILL:
                getBoardTileConverted(piece.getOldCoord()).setPiece(null);
                piece.move(newCoord);
                getBoardTile(newCoord).setPiece(piece);
                turn = false;

                Piece otherPiece = movePiece.getPiece();
                getBoardTileConverted(otherPiece.getOldCoord()).setPiece(null);
                Platform.runLater(() -> pieceGroup.getChildren().remove(otherPiece));
                if((newCoord.getY() == 0 && piece.getType() == PieceType.WHITE) || (newCoord.getY() == 7 && piece.getType() == PieceType.RED)){
                    Platform.runLater(piece::promotePiece);
                }
                break;
        }
    }

    public Tile getBoardTile(Coordinates coordinates){
        return board[coordinates.getX()][coordinates.getY()];
    }
    public Tile getBoardTileConverted(Coordinates coordinates){
        return board[convertPixToCoord(coordinates.getX())][convertPixToCoord(coordinates.getY())];
    }

}