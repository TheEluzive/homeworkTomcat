package org.example.app.exception;

public class CardsGetAllException extends RuntimeException{
    public CardsGetAllException() {
    }

    public CardsGetAllException(String message) {
        super(message);
    }

    public CardsGetAllException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardsGetAllException(Throwable cause) {
        super(cause);
    }

    public CardsGetAllException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
