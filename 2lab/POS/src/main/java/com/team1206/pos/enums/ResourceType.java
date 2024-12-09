package com.team1206.pos.enums;

import lombok.Getter;

@Getter
public enum ResourceType {
    MERCHANT("Merchant"),
    USER("User"),
    CATEGORY("Product Category"),
    PRODUCT("Product"),
    CHARGE("Charge");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }

}
