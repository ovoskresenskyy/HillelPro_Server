package org.example.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerInputListener extends Thread {

    @Override
    public void run() {
        while (true) {
            String input;
            try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in))) {
                input = inputReader.readLine();

                if (input.equals("-close")) MyServerService.getInstance().closeServer();

                MenuService.getInstance().sendToEveryone("Server", input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
