package com.example.checkersjavafxsockets.Server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {
    public static final int PORT = 1410;
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        Server server = new Server(serverSocket);
        System.out.println("Server is listening at port: " + PORT);
        server.startServer();

    }
}
