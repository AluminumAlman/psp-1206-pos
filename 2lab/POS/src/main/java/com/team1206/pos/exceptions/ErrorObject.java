package com.team1206.pos.exceptions;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorObject {
    private Integer statusCode;
    private String identifier;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> details;
}
