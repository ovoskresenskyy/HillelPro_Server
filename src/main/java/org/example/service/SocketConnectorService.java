package org.example.service;

import org.example.NotMyExecutor;
import org.example.model.SocketConnector;
import org.example.model.MyServer;

import java.io.*;

public class SocketConnectorService {

    private SocketConnectorService() {
    }

    private static class ClientConnectorServiceHolder {
        private final static SocketConnectorService instance = new SocketConnectorService();
    }

    public static SocketConnectorService getInstance() {
        return SocketConnectorService.ClientConnectorServiceHolder.instance;
    }

    public NotMyExecutor receiveFile(SocketConnector socketConnector) {
        return () -> {
            try (OutputStream fileWriter = new FileOutputStream("src/main/resources/" + socketConnector + "_received_file")) {
                InputStream input = socketConnector.getSocket().getInputStream();

                byte[] buffer = new byte[1024];
                fileWriter.write(buffer, 0, input.read(buffer));
                fileWriter.flush();

                System.out.println("Received file from " + socketConnector);
            } catch (IOException e) {
                System.out.println("SocketTools.receive(): IO error.");
                e.printStackTrace(System.out);
            }
        };
    }

    public NotMyExecutor sendMessageForAllConnected(String sender, String message) {
        return () -> MenuService.getInstance().sendToEveryone(sender, message);
    }

    public NotMyExecutor closeConnection(SocketConnector socketConnector) {
        return () -> {
            MenuService.getInstance().sendToEveryone("Server", socketConnector + " is leaving our server!");

            MyServer.getInstance()
                    .getSocketConnectors()
                    .remove(socketConnector);
            closeAllResources(socketConnector);
        };
    }

    private void closeAllResources(SocketConnector socketConnector) {
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

    public String parseUserInput(String userInput) {
        String[] words = userInput.split(" ");

        String command = "";
        if (words.length > 0) command = words[0];

        return command;
    }
}
