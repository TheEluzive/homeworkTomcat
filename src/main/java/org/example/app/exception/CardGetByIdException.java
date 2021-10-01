package org.example.app.exception;

public class CardGetByIdException extends RuntimeException {
    public CardGetByIdException() {
    }

    public CardGetByIdException(String message) {
        super(message);
    }

    public CardGetByIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardGetByIdException(Throwable cause) {
        super(cause);
    }

    public CardGetByIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
