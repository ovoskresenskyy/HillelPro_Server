package org.example.service;

import org.apache.commons.lang3.tuple.Pair;
import org.example.enums.Command;
import org.example.model.SocketConnector;
import org.example.model.MyServer;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MenuService {

    private final List<Pair<String, String>> chatHistory = new CopyOnWriteArrayList<>();

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

        printHistory(recipient);
    }

    public void sendPrivateMessage(String sender, SocketConnector recipient, String message) {
        try {
            recipient.getSender().write(sender + message + System.lineSeparator());
            recipient.getSender().flush();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Can't send private message for " + recipient);
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

        chatHistory.add(Pair.of(sender, message));
    }

    private String issueSenderName(String senderName) {
        return senderName.equals("") ? senderName : "[" + senderName + "]: ";
    }

    private void printHistory(SocketConnector recipient) {

        sendPrivateMessage("", recipient, "\nLast messages:\n");
        chatHistory.stream()
                .skip(chatHistory.size() > 10 ? chatHistory.size() - 10 : 0)
                .forEach(message -> sendPrivateMessage(issueSenderName(message.getLeft()), recipient, message.getRight()));
    }
}
