package org.example.app.exception;

public class GetAllCardsException extends RuntimeException{
    public GetAllCardsException() {
    }

    public GetAllCardsException(String message) {
        super(message);
    }

    public GetAllCardsException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetAllCardsException(Throwable cause) {
        super(cause);
    }

    public GetAllCardsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
