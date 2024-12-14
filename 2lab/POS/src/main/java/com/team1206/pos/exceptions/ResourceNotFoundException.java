package com.team1206.pos.exceptions;

import com.team1206.pos.common.enums.ResourceType;
import lombok.Getter;

import java.io.Serial;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String uuid;
    private final ResourceType resourceType;

    public ResourceNotFoundException(ResourceType resourceType, String uuid) {
        super(String.format("%s with ID %s not found", resourceType, uuid)); // Call parent constructor with formatted message
        this.resourceType = resourceType;
        this.uuid = uuid;
    }

}

