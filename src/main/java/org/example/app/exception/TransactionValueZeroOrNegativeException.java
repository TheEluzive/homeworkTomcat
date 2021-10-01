package org.example.app.exception;

public class TransactionValueZeroOrNegativeException extends RuntimeException{
    public TransactionValueZeroOrNegativeException() {
    }

    public TransactionValueZeroOrNegativeException(String message) {
        super(message);
    }

    public TransactionValueZeroOrNegativeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionValueZeroOrNegativeException(Throwable cause) {
        super(cause);
    }

    public TransactionValueZeroOrNegativeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
