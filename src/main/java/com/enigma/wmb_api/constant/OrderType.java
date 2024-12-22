package com.enigma.wmb_api.constant;

public enum OrderType {
    DINE_IN("DI_TEMPAT"),
    TAKE_AWAY("BUNGKUS");


    private String value;

    OrderType(String value) {
        this.value = value;
    }

    public static OrderType fromValue(String value) {
        for (OrderType orderType : values()) {
            if (orderType.value.equalsIgnoreCase(value)) {
                return orderType;
            }
        }
        return null;
    }
}
