package org.example.app.exception;

public class TokenDeprecatedException extends RuntimeException {
    public TokenDeprecatedException() {
    }

    public TokenDeprecatedException(String message) {
        super(message);
    }

    public TokenDeprecatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenDeprecatedException(Throwable cause) {
        super(cause);
    }

    public TokenDeprecatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
