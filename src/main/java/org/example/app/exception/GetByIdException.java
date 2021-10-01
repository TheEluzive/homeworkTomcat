package org.example.app.exception;

public class GetByIdException extends RuntimeException {
    public GetByIdException() {
    }

    public GetByIdException(String message) {
        super(message);
    }

    public GetByIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetByIdException(Throwable cause) {
        super(cause);
    }

    public GetByIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
