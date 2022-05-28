package com.example.checkersjavafxsockets.Server;

import com.example.checkersjavafxsockets.Checkers;
import com.example.checkersjavafxsockets.Game.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Checkers checkers;
    private final Socket socket1;
    private final BufferedReader bufferedReader1;
    private final BufferedWriter bufferedWriter1;
    private final Socket socket2;
    private final BufferedReader bufferedReader2;
    private final BufferedWriter bufferedWriter2;
    private final MoveActions moveActions;

    public ClientHandler(Socket socket1, Socket socket2) throws IOException{
        this.moveActions = new MoveActions(checkers);
        this.checkers = new Checkers();
        this.socket1 = socket1;
        this.bufferedReader1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
        this.bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
        this.bufferedWriter1.write("1");
        this.bufferedWriter1.newLine();
        this.bufferedWriter1.flush();

        this.socket2 = socket2;
        this.bufferedReader2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
        this.bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream()));
        this.bufferedWriter2.write("2");
        this.bufferedWriter2.newLine();
        this.bufferedWriter2.flush();
    }

    private boolean proccessMove() throws IOException{
        try {
            String message = null;
            if (checkers.getDirection() == MoveType.REDNOW) {
                sendMessage("TURN", bufferedWriter1);
                message = bufferedReader1.readLine();
            } else if (checkers.getDirection() == MoveType.WHITENOW) {
                sendMessage("TURN", bufferedWriter2);
                message = bufferedReader2.readLine();
            }
            if(message == null){
                return false;
            }
            System.out.println(message);
            String[] data = message.split(" ");
            int oldX = Integer.parseInt(data[0]);
            int oldY = Integer.parseInt(data[1]);
            int newX = Integer.parseInt(data[2]);
            int newY = Integer.parseInt(data[3]);
            if (checkers.getBoard()[oldX][oldY].getPiece() == null) {
                return false;
            }

            MovePiece movePiece = moveActions.tryMove(checkers.getBoard()[oldX][oldY].getPiece(), newX, newY);
            if (movePiece.getMoveType() == MoveType.NONE) {
                return false;
            }
            moveActions.makeMove(newX, newY, checkers.getBoard()[oldX][oldY].getPiece(), movePiece);
            String terminalMessage = Creator.createTerminalMessage(newX, newY, checkers.getBoard()[oldX][oldY].getPiece(), movePiece);
            sendMessage(terminalMessage, bufferedWriter1);
            sendMessage(terminalMessage, bufferedWriter2);

            return true;
        } catch (IOException e){
            closeAll();
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void run(){
        checkers.createSceneContent();
        while(socket1.isConnected() && (checkers.getRedPieces() > 0 || checkers.getWhitePieces() > 0)){
            try {
                proccessMove();
            } catch (IOException e) {
                closeAll();
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String message, BufferedWriter bufferedWriter){
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeAll();
        }
    }


    public void closeAll() {
        try {
            if (bufferedReader1 != null) {
                bufferedReader1.close();
            }
            if (bufferedWriter1 != null) {
                bufferedWriter1.close();
            }
            if (socket1 != null) {
                socket1.close();
            }
            if (bufferedReader2 != null) {
                bufferedReader2.close();
            }
            if (bufferedWriter2 != null) {
                bufferedWriter2.close();
            }
            if (socket2 != null) {
                socket2.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
