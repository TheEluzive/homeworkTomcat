package org.example.app.exception;

public class TokenRecoveryException extends RuntimeException{
    public TokenRecoveryException() {
    }

    public TokenRecoveryException(String message) {
        super(message);
    }

    public TokenRecoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenRecoveryException(Throwable cause) {
        super(cause);
    }

    public TokenRecoveryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
