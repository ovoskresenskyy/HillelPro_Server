package org.example.service;

import org.example.enums.Command;
import org.example.model.SocketConnector;
import org.example.model.MyServer;

import java.io.IOException;
import java.util.EnumSet;

public class MenuService {

    private MenuService() {
    }

    private static class MenuServiceHolder {
        private final static MenuService instance = new MenuService();
    }

    public static MenuService getInstance() {
        return MenuServiceHolder.instance;
    }

    public void printCommandMenu(SocketConnector recipient) {

        StringBuilder greeting = new StringBuilder("Welcome to our server!")
                .append(System.lineSeparator())
                .append("Known commands:");

        EnumSet.allOf(Command.class)
                .forEach(command -> greeting
                        .append(System.lineSeparator())
                        .append("-> ")
                        .append(command)
                        .append(" ")
                        .append(command.getDescription()));

        sendPrivateMessage("", recipient, greeting.toString());
    }

    public void sendPrivateMessage(String sender, SocketConnector recipient, String message) {
        try {
            recipient.getSender().write(sender + message + System.lineSeparator());
            recipient.getSender().flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't send message.");
        }
    }

    public void sendToEveryone(String sender, String message) {

        String senderName = issueSenderName(sender);

        System.out.println(senderName + message);

        MyServer.getInstance()
                .getSocketConnectors().stream()
                .filter(clientConnector -> clientConnector.getThread().isAlive())
                .filter(clientConnector -> clientConnector.getSocket().isConnected())
                .forEach(recipient -> sendPrivateMessage(senderName, recipient, message));
    }

    private String issueSenderName(String senderName) {
        return senderName.equals("") ? senderName : "[" + senderName + "]: ";
    }
}
