package org.example.service;

import org.example.exceptions.CantSetConnectionWithSocketException;
import org.example.model.MyServer;
import org.example.model.SocketConnector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionsWaiter extends Thread {

    private int sessionNumber;
    private final ServerSocket serverSocket;

    public ServerConnectionsWaiter(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        MenuService menuService = MenuService.getInstance();

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    SocketConnector clientConnector = SocketConnector.createAndStart(clientSocket, "Client-" + (++sessionNumber));
                    MyServer.getInstance()
                            .getSocketConnectors()
                            .add(clientConnector);

                    menuService.sendToEveryone("", clientConnector + " connected our server.");
                } catch (CantSetConnectionWithSocketException e) {
                    e.printStackTrace();
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection is broken.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
