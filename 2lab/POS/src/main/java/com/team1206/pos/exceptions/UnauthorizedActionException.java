package com.team1206.pos.exceptions;

import lombok.Getter;

@Getter
public class UnauthorizedActionException extends RuntimeException {
    private final String action;

    public UnauthorizedActionException(String message, String action) {
        super(message);
        this.action = action;
    }
}

