package org.example.app.exception;

public class CardOwnerNotFoundException extends RuntimeException {
    public CardOwnerNotFoundException() {
    }

    public CardOwnerNotFoundException(String message) {
        super(message);
    }

    public CardOwnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardOwnerNotFoundException(Throwable cause) {
        super(cause);
    }

    public CardOwnerNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
