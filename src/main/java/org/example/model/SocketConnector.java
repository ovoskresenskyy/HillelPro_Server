package org.example.model;

import org.apache.commons.lang3.tuple.Pair;
import org.example.NotMyExecutor;
import org.example.enums.Command;
import lombok.Data;
import org.example.exceptions.CantSetConnectionWithSocketException;
import org.example.exceptions.UserInputIsNullException;
import org.example.service.SocketConnectorService;
import org.example.service.MenuService;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Map;

@Data
public class SocketConnector implements Runnable {

    private Thread thread;
    private final Socket socket;
    private final Date connectionTimeStamp;
    private final BufferedReader reader;
    private final BufferedWriter sender;
    private boolean running;

    public static SocketConnector createAndStart(Socket socket, String name) throws CantSetConnectionWithSocketException {

        SocketConnector clientConnector = new SocketConnector(socket, name);
        clientConnector.thread.start();

        MenuService.getInstance().printCommandMenu(clientConnector);

        return clientConnector;
    }

    private SocketConnector(Socket socket, String name) throws CantSetConnectionWithSocketException {

        thread = new Thread(this, name);
        this.socket = socket;
        connectionTimeStamp = new Date();
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sender = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new CantSetConnectionWithSocketException();
        }
    }

    @Override
    public void run() {

        SocketConnectorService socketConnectorService = SocketConnectorService.getInstance();
        Map<Command, NotMyExecutor> commandHandler = initializeCommands(socketConnectorService);
        running = true;

        String userInput;
        try {
            while (running) {
                userInput = reader.readLine();
                if (userInput == null) throw new UserInputIsNullException();

                Pair<String, String> commandWithParameter = socketConnectorService.parseUserInput(userInput);

                commandHandler
                        .getOrDefault(Command.getByName(commandWithParameter.getLeft())
                                , socketConnectorService.sendMessageForAllConnected(thread.getName(), commandWithParameter.getLeft()))
                        .execute(commandWithParameter.getRight());
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            System.out.println(thread.getName() + " socket is abandoned.");
        }
    }

    private Map<Command, NotMyExecutor> initializeCommands(SocketConnectorService clientConnectorService) {
        return Map.of(
                Command.SEND_FILE, clientConnectorService.sendFile(),
                Command.EXIT, clientConnectorService.closeConnection(this)
        );
    }

    @Override
    public String toString() {
        return thread.getName();
    }
}