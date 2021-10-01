package org.example.app.exception;

public class CardNotOrderedException extends RuntimeException {
    public CardNotOrderedException() {
    }

    public CardNotOrderedException(String message) {
        super(message);
    }

    public CardNotOrderedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardNotOrderedException(Throwable cause) {
        super(cause);
    }

    public CardNotOrderedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
