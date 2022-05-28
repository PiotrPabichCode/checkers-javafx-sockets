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
import javafx.scene.control.Label;
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

    public Tile[][] board = new Tile[MAX_SIZE][MAX_SIZE];
    public MoveActions moveActions;

    private final Group tileGroup = new Group();
    private final Group pieceGroup = new Group();
    private final Label label = new Label();
    private final Timer timer = new Timer();
    private int whitePieces = 0;
    private int redPieces = 0;
    private boolean winner = false;
    private float time = 0;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private MoveType direction = MoveType.WHITENOW;
    public boolean turn = false;

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
        } catch (IOException e){
            closeAll();
        }
        this.moveActions = new MoveActions(this);
        timeCount();
//        listenServer();
        Scene scene = new Scene(createSceneContent());
        stage.setTitle("Checkers");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    private void listenServer(){
        new Thread(() -> {
            String message;
            while(socket.isConnected() && !winner) {
                try {
                    message = bufferedReader.readLine();
                    System.out.println(message);
                    if(message.startsWith("PING")){
                        turn = true;
                    }
                    else{
                        String[] data = message.split(" ");
                        int oldX = Integer.parseInt(data[0]);
                        int oldY = Integer.parseInt(data[1]);
                        int newX = Integer.parseInt(data[2]);
                        int newY = Integer.parseInt(data[3]);
                        String moveType = data[4];

                        Piece piece = board[oldX][oldY].getPiece();
                        switch (moveType) {
                            case "NONE":
                                moveActions.makeMove(newX, newY, piece, new MovePiece(MoveType.NONE));
                                break;
                            case "NORMAL":
                                moveActions.makeMove(newX, newY, piece, new MovePiece(MoveType.NORMAL));
                                break;
                            case "KILL":
                                int killX = Integer.parseInt(data[5]);
                                int killY = Integer.parseInt(data[6]);
                                Piece pieceDestroyed = board[killX][killY].getPiece();
                                moveActions.makeMove(newX, newY, piece, new MovePiece(MoveType.KILL, pieceDestroyed));
                                pieceGroup.getChildren().remove(pieceDestroyed);
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

    public Piece makePiece(PieceType type, int x, int y){
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e -> {
            int newX = convertPixToCoord(piece.getLayoutX());
            int newY = convertPixToCoord(piece.getLayoutY());
            moveActions.requestMove(piece, newX, newY);
        });

        return piece;
    }

    public void timeCount(){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            if(winner){
                Platform.runLater(() -> timer.set((whitePieces == 0) ? "Red won!" : "White won"));
                executorService.shutdown();
                return;
            }
            if(turn){
                Platform.runLater(() -> timer.set("Playtime: " + String.format("%.1f", time) + "s."));
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

    public int getWhitePieces(){
        return this.whitePieces;
    }
    public int getRedPieces(){
        return this.redPieces;
    }
    public MoveType getDirection(){
        return this.direction;
    }
    public void setDirection(MoveType moveType){
        this.direction = moveType;
    }

    public Tile[][] getBoard() {
        return this.board;
    }
    public void setWinner(){
        this.winner = true;
    }
    public void setWhitePieces(){
        this.whitePieces--;
    }
    public void setRedPieces(){
        this.redPieces--;
    }
    public void setTurn(boolean state){
        this.turn = state;
    }
    public void removePiece(Piece piece){
        this.pieceGroup.getChildren().remove(piece);
    }
}