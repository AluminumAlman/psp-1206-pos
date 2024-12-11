package com.team1206.pos.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class IllegalStateExceptionWithId extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2L;
    private final String uuid;

    public IllegalStateExceptionWithId(String message, String uuid) {
        super(message);
        this.uuid = uuid;
    }
}
