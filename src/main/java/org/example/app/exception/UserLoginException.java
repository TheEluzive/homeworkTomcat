package org.example.app.exception;

public class UserLoginException extends RuntimeException{
    public UserLoginException() {
    }

    public UserLoginException(String message) {
        super(message);
    }

    public UserLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserLoginException(Throwable cause) {
        super(cause);
    }

    public UserLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
