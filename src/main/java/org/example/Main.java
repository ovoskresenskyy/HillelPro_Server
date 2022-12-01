package org.example;

import org.example.model.MyServer;

public class Main {
    public static void main(String[] args) {

        MyServer myServer = MyServer.getInstance();
        myServer.start();
    }
}
