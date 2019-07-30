package ilia.nemankov.togrofbot.database.repository;

public class ItemNotPresentedException extends Exception {

    public ItemNotPresentedException() {

    }

    public ItemNotPresentedException(String message) {
        super(message);
    }

    public ItemNotPresentedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotPresentedException(Throwable cause) {
        super(cause);
    }

}
