package com.tpe.cinetime.exception;

public class EmailSendException extends RuntimeException{

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailSendException(String message) {
        super(message);
    }
}
