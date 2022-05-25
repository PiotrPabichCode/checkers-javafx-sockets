package com.example.checkersjavafxsockets;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static com.example.checkersjavafxsockets.Checkers.MAX_SIZE;

public class ClientHandler implements Runnable{

    private final Tile[][] board = new Tile[MAX_SIZE][MAX_SIZE];
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private int redPieces = 0;
    private int whitePieces = 0;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            clientHandlers.add(this);
        } catch (IOException e){
            closeAll();
        }
    }

    @Override
    public void run(){
        createSceneContent();
    }

    public void createSceneContent(){
        for(int y = 0; y < MAX_SIZE; y++){
            for(int x = 0; x < MAX_SIZE; x++){
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;


                Piece piece = null;
                if(y < MAX_SIZE / 2 - 1 && (x + y) % 2 != 0){
                    piece = new Piece(PieceType.RED, x, y);
                }
                if(y >= MAX_SIZE / 2 + 1 && (x + y) % 2 != 0){
                    piece = new Piece(PieceType.WHITE, x, y);
                }

                if(piece != null){
                    tile.setPiece(piece);
                }
            }
        }
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

    public void addClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            clientHandlers.add(this);
        } catch (IOException e){
            closeAll();
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
    }

    public void closeAll() {
        removeClientHandler();
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
