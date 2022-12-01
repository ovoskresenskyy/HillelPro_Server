package org.example.service;

import org.example.NotMyExecutor;
import org.example.model.SocketConnector;
import org.example.model.MyServer;

import java.io.IOException;

public class SocketConnectorService {

    private SocketConnectorService(){}

    private static class ClientConnectorServiceHolder {
        private final static SocketConnectorService instance = new SocketConnectorService();
    }

    public static SocketConnectorService getInstance() {
        return SocketConnectorService.ClientConnectorServiceHolder.instance;
    }

    public NotMyExecutor sendFile() {
        return () -> System.out.println("send file");
    }

    public NotMyExecutor sendMessageForAllConnected(String sender, String message) {
        return () -> MenuService.getInstance().sendToEveryone(sender, message);
    }

    public NotMyExecutor closeConnection(SocketConnector socketConnector) {
        return () -> {
            MyServer.getInstance()
                    .getSocketConnectors()
                    .remove(socketConnector);
            closeAllResources(socketConnector);

            MenuService.getInstance().sendToEveryone("", socketConnector + " leave our server.");
            System.out.println(socketConnector + " is disconnected.");
        };
    }

    private void closeAllResources(SocketConnector socketConnector){
        try {
            socketConnector.getReader().close();
            socketConnector.getSender().close();
            socketConnector.getSocket().close();
            socketConnector.stopThread();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Trying to close socket connector. Socket is not available.");
        }
    }

}
