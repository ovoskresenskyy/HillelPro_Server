package org.example.exceptions;

import java.io.IOException;

public class CantSetConnectionWithSocketException extends IOException {

    public CantSetConnectionWithSocketException() {
        super("Socket is closed or not connected.");
    }
}
