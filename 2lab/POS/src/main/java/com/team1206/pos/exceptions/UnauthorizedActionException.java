package com.team1206.pos.exceptions;

import lombok.Getter;

@Getter
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}

