package ru.saveldu.exceptions;

public class NoUserInChat extends FoloBotException{

    public NoUserInChat() {}
    public NoUserInChat(String message) {
        super(message);
    }
}
