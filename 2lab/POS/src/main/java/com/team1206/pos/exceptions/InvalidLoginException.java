package com.team1206.pos.exceptions;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("Invalid login credentials");
    }
}
