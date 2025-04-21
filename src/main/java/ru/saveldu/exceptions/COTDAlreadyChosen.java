package ru.saveldu.exceptions;



public class COTDAlreadyChosen extends FoloBotException{
    public COTDAlreadyChosen() {
        super();
    }
    public COTDAlreadyChosen(String message) {
        super(message);
    }
}
