package com.team1206.pos.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorObject {
    private Integer statusCode;
    private String uuid;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
