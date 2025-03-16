package ru.saveldu.exceptions;

public class FoloBotException extends RuntimeException {

    public FoloBotException() {
        super();
    }
    public FoloBotException(String message) {
        super(message);
    }
    public FoloBotException(String message, Throwable cause) {
        super(message, cause);
    }

}
