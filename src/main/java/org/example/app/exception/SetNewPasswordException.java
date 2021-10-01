package org.example.app.exception;

public class SetNewPasswordException extends RuntimeException{
    public SetNewPasswordException() {
    }

    public SetNewPasswordException(String message) {
        super(message);
    }

    public SetNewPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetNewPasswordException(Throwable cause) {
        super(cause);
    }

    public SetNewPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
