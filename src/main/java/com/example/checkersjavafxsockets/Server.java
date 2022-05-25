package com.example.checkersjavafxsockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        while(!serverSocket.isClosed()){
            try{
                Socket socket1 = serverSocket.accept();
                Socket socket2 = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket1, socket2);
                Thread thread = new Thread(clientHandler);
                thread.start();
            } catch (IOException e){
                closeServerSocket();
            }
        }
    }

    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
