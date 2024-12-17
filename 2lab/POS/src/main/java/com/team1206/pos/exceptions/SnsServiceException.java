package com.team1206.pos.exceptions;

public class SnsServiceException extends RuntimeException {
    public SnsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
