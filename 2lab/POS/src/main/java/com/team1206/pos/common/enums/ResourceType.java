package com.team1206.pos.common.enums;

import lombok.Getter;

@Getter
public enum ResourceType {
    MERCHANT("Merchant"),
    USER("User"),
    SERVICE("Service"),
    RESERVATION("Reservation"),
    PRODUCT_CATEGORY("Product Category"),
    PRODUCT("Product"),
    PRODUCT_VARIATION("Product Variation"),
    CHARGE("Charge"),
    DISCOUNT("Discount"),
    ORDER("Order"),
    TRANSACTION("Transaction");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }

}
