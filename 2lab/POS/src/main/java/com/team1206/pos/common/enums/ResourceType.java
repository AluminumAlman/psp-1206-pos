package com.team1206.pos.common.enums;

import lombok.Getter;

@Getter
public enum ResourceType {
    MERCHANT("Merchant"),
    USER("User"),
    SERVICE("Service"),
    RESERVATION("Reservation"),
    SCHEDULE("Schedule"),
    PRODUCT_CATEGORY("Product Category"),
    PRODUCT("Product"),
    PRODUCT_VARIATION("Product Variation"),
    CHARGE("Charge"),
    DISCOUNT("Discount"),
    ORDER("Order"),
    ORDER_CHARGE("Order Charge"),
    TRANSACTION("Transaction"),
    ORDER_ITEM("Order Item");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }

}
