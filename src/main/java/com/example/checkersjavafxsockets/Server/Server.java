package com.example.checkersjavafxsockets.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                Socket socket1 = getSocket();
                Socket socket2 = getSocket();
                ClientHandler clientHandler = new ClientHandler(socket1, socket2);
                Thread thread = new Thread(clientHandler);
                thread.start();
            } catch (IOException e){
                closeServerSocket();
            }
        }
    }

    private Socket getSocket() throws IOException{
        Socket socket = null;
        while(socket == null){
            socket = serverSocket.accept();
            if (new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine().startsWith("multiplayer")) {
                break;
            } else {
                ClientHandler clientHandler = new ClientHandler(socket, null);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }
        return socket;
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
