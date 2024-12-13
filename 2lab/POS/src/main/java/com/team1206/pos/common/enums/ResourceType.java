package com.team1206.pos.common.enums;

import lombok.Getter;

@Getter
public enum ResourceType {
    MERCHANT("Merchant"),
    USER("User"),
    SERVICE("Service"),
    PRODUCT_CATEGORY("Product Category"),
    PRODUCT("Product"),
    CHARGE("Charge"),
    DISCOUNT("Discount");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }

}
