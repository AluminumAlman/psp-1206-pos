package com.team1206.pos.common.enums;

import lombok.Getter;

@Getter
public enum ResourceType {
    MERCHANT("Merchant"),
    USER("User"),
    SERVICE("Service");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }

}
