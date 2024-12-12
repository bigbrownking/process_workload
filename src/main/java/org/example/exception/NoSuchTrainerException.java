package org.example.exception;

public class NoSuchTrainerException extends Exception{
    public NoSuchTrainerException() {
    }

    public NoSuchTrainerException(String message) {
        super(message);
    }
}
