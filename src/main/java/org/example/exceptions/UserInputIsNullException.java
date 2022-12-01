package org.example.exceptions;

public class UserInputIsNullException extends RuntimeException {

    public UserInputIsNullException() {
        super("User closed application incorrectly. Command is null.");
    }
}
