package com.example.checkersjavafxsockets.Server;

import com.example.checkersjavafxsockets.Coordinates;
import com.example.checkersjavafxsockets.Game.*;
import com.example.checkersjavafxsockets.UI.Piece;
import com.example.checkersjavafxsockets.UI.PieceType;
import com.example.checkersjavafxsockets.UI.Tile;

import java.io.*;
import java.net.Socket;

import static com.example.checkersjavafxsockets.Checkers.MAX_SIZE;
import static com.example.checkersjavafxsockets.Game.Creator.convertPixToCoord;

public class ClientHandler implements Runnable{
    private final Tile[][] board = new Tile[MAX_SIZE][MAX_SIZE];
    private final Socket socket1;
    private final BufferedReader bufferedReader1;
    private final BufferedWriter bufferedWriter1;
    private final Socket socket2;
    private final BufferedReader bufferedReader2;
    private final BufferedWriter bufferedWriter2;
    private int whitePieces = 0;
    private int redPieces = 0;

    public ClientHandler(Socket socket1, Socket socket2) throws IOException{
        try{
            this.socket1 = socket1;
            this.bufferedReader1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            this.bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
            sendMessage("1", bufferedWriter1);

            this.socket2 = socket2;
            if(socket2 != null){
                this.bufferedReader2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
                this.bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(socket2.getOutputStream()));
                sendMessage("2", bufferedWriter2);
            }else{
                bufferedWriter2 = null;
                bufferedReader2 = null;
            }
        } catch (IOException e){
            closeAll();
            throw e;
        }
    }

    private boolean analyseMove(int moveDirection) throws IOException{
        try {
            BufferedWriter fromBufferedWriter;
            BufferedReader fromBufferedReader;
            BufferedWriter outBufferedWriter;
            if(moveDirection == -1){
                fromBufferedReader = bufferedReader1;
                fromBufferedWriter = bufferedWriter1;
                outBufferedWriter = bufferedWriter2;
            } else{
                fromBufferedReader = bufferedReader2;
                fromBufferedWriter = bufferedWriter2;
                outBufferedWriter = bufferedWriter1;
            }
            if(fromBufferedWriter != null){
                sendMessage("TURN", fromBufferedWriter);
            }
            String message;
            if(fromBufferedReader != null){
                message = fromBufferedReader.readLine();
            }
            else{
                message = Creator.generateBotMove();
            }

            System.out.println(message);
            String[] data = message.split(" ");
            Coordinates oldCoord = new Coordinates(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
            Coordinates newCoord = new Coordinates(Integer.parseInt(data[2]), Integer.parseInt(data[3]));

            if(getBoardTile(oldCoord).getPiece() == null){
                return false;
            }

            MovePiece movePiece = tryMove(getBoardTile(oldCoord).getPiece(), newCoord);
            if(Math.signum(getBoardTile(oldCoord).getPiece().getType().moveDirection) == Math.signum(moveDirection)){
                if(fromBufferedWriter != null){
                    String terminalMessage = Creator.createTerminalMessage(newCoord.getX(), newCoord.getY(), getBoardTile(oldCoord).getPiece(), new MovePiece(MoveType.NONE));
                    sendMessage(terminalMessage, fromBufferedWriter);
                }
                return false;
            }
            String terminalMessage = Creator.createTerminalMessage(newCoord.getX(), newCoord.getY(), getBoardTile(oldCoord).getPiece(), movePiece);
            makeMove(newCoord, getBoardTile(oldCoord).getPiece(), movePiece);
            if(outBufferedWriter != null){
                sendMessage(terminalMessage, outBufferedWriter);
            }
            if(fromBufferedWriter != null){
                sendMessage(terminalMessage, fromBufferedWriter);
            }

            if(whitePieces == 0 || redPieces == 0){
                String end;
                if(whitePieces == 0){
                    end = "0 0 0 0 WINNER_RED";
                } else{
                    end = "0 0 0 0 WINNER_WHITE";
                }
                if(outBufferedWriter != null){
                    sendMessage(end, outBufferedWriter);
                }
                if(fromBufferedWriter != null){
                    sendMessage(end, fromBufferedWriter);
                }
            }

            return movePiece.getMoveType() != MoveType.NONE;
        } catch (IOException e){
            closeAll();
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void run(){
        createSceneContent();
        int i = 1;
        while(socket1.isConnected() && (socket2 == null || socket2.isConnected()) && whitePieces * redPieces > 0){
            try {
                if(analyseMove(i % 2 * 2 - 1)){
                    i++;
                }
            } catch (IOException e) {
                closeAll();
                e.printStackTrace();
            }
        }
    }

    private void createSceneContent(){
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                Piece piece = null;
                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = new Piece(PieceType.RED, x, y);
                    redPieces++;
                } else if (y >= 5 && (x + y) % 2 != 0) {
                    piece = new Piece(PieceType.WHITE, x, y);
                    whitePieces++;
                }

                if (piece != null) {
                    tile.setPiece(piece);
                }
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

    public MovePiece tryMove(Piece piece, Coordinates newCoord){
        MovePiece movePiece = new MovePiece(MoveType.NONE);
        if(getBoardTile(newCoord).hasPiece() || (newCoord.getX() + newCoord.getY()) % 2 == 0){
            return movePiece;
        }
        Coordinates oldCoord = new Coordinates(convertPixToCoord(piece.getOldX()), convertPixToCoord(piece.getOldY()));
        if(tryNormalMove(piece, oldCoord, newCoord, movePiece)){
            return movePiece;
        }
        if(tryKillMove(piece, oldCoord, newCoord, movePiece)){
            return movePiece;
        }
        return new MovePiece(MoveType.NONE);
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
                if((newCoord.getY() == 0 && piece.getType() == PieceType.WHITE) || (newCoord.getY() == 7 && piece.getType() == PieceType.RED)){
                    piece.promotePiece();
                }
                break;
            case KILL:
                getBoardTileConverted(piece.getOldCoord()).setPiece(null);
                piece.move(newCoord);
                getBoardTile(newCoord).setPiece(piece);

                Piece otherPiece = movePiece.getPiece();
                getBoardTileConverted(otherPiece.getOldCoord()).setPiece(null);
                if(piece.getType() == PieceType.RED || piece.getType() == PieceType.RED_PROMOTED){
                    whitePieces--;
                } else{
                    redPieces--;
                }
                if((newCoord.getY() == 0 && piece.getType() == PieceType.WHITE) || (newCoord.getY() == 7 && piece.getType() == PieceType.RED)){
                    piece.promotePiece();
                }
                break;
        }
    }

    public boolean tryNormalMove(Piece piece, Coordinates oldCoord, Coordinates newCoord, MovePiece movePiece){
        int subtractX = newCoord.subtractX(oldCoord.getX());
        int subtractY = newCoord.subtractY(oldCoord.getY());
        if (Math.abs(subtractX) == 1 && subtractY == piece.getType().moveDirection) {
            movePiece.setMoveType(MoveType.NORMAL);
            return true;
        }
        if((piece.getType() == PieceType.RED_PROMOTED || piece.getType() == PieceType.WHITE_PROMOTED) && (Math.abs(subtractX) == 1 && Math.abs(subtractY) == 1)){
            movePiece.setMoveType(MoveType.NORMAL);
            return true;
        }
        return false;
    }
    public boolean tryKillMove(Piece piece, Coordinates oldCoord, Coordinates newCoord, MovePiece movePiece){
        int subtractX = newCoord.subtractX(oldCoord.getX());
        int subtractY = newCoord.subtractY(oldCoord.getY());
        if(Math.abs(subtractX) == 2 && Math.abs(subtractY) == 2){
            Coordinates middleCoord = new Coordinates(oldCoord.getX() + subtractX / 2, oldCoord.getY() + subtractY / 2);
            if(getBoardTile(middleCoord).hasPiece() && getBoardTile(middleCoord).getPiece().getType() != piece.getType()) {
                movePiece.setMoveType(MoveType.KILL);
                movePiece.setPiece(getBoardTile(middleCoord).getPiece());
                return true;
            }
        }
        return false;
    }

    public Tile getBoardTile(Coordinates coordinates){
        return board[coordinates.getX()][coordinates.getY()];
    }
    public Tile getBoardTileConverted(Coordinates coordinates){
        return board[convertPixToCoord(coordinates.getX())][convertPixToCoord(coordinates.getY())];
    }
}
