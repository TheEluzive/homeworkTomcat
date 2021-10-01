package org.example.app.exception;

public class CardBlockByIdException extends RuntimeException{
    public CardBlockByIdException() {
    }

    public CardBlockByIdException(String message) {
        super(message);
    }

    public CardBlockByIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardBlockByIdException(Throwable cause) {
        super(cause);
    }

    public CardBlockByIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
