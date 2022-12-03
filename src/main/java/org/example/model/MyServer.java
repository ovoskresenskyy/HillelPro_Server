package org.example.model;

import org.example.service.ServerConnectionsWaiter;
import org.example.service.ServerInputListener;

import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyServer {

    private final List<SocketConnector> socketConnectors;

    private MyServer() {
        socketConnectors = new CopyOnWriteArrayList<>();
    }

    private static class MyServerHolder {
        private final static MyServer instance = new MyServer();
    }

    public static MyServer getInstance() {
        return MyServer.MyServerHolder.instance;
    }

    public List<SocketConnector> getSocketConnectors() {
        return socketConnectors;
    }

    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(10160);
            System.out.println("Server was started!");

            ServerConnectionsWaiter serverConnectionsWaiter = new ServerConnectionsWaiter(serverSocket);
            serverConnectionsWaiter.start();

            ServerInputListener serverInputListener = new ServerInputListener();
            serverInputListener.start();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server crashed.");
            System.exit(1);
        }

    }
}
