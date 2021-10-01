package org.example.app.exception;

public class CardOrderNotSuccessfullyException extends RuntimeException {
    public CardOrderNotSuccessfullyException() {
    }

    public CardOrderNotSuccessfullyException(String message) {
        super(message);
    }

    public CardOrderNotSuccessfullyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardOrderNotSuccessfullyException(Throwable cause) {
        super(cause);
    }

    public CardOrderNotSuccessfullyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
