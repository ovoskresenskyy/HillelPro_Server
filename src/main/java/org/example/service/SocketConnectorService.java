package org.example.service;

import org.apache.commons.lang3.tuple.Pair;
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

    public NotMyExecutor receiveFile() {
        return () -> System.out.println("");
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
        };
    }

    private void closeAllResources(SocketConnector socketConnector){
        try {
            stopThread(socketConnector);
            socketConnector.getReader().close();
            socketConnector.getSender().close();
            socketConnector.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Trying to close socket connector. Socket is not available.");
        }
    }

    private void stopThread(SocketConnector socketConnector) {
        socketConnector.setRunning(false);
    }

}
