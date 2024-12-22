package com.enigma.wmb_api.constant;

public enum OrderStatus {
    DRAFT("DRAFT"),
    PENDING("PROSES"),
    PAID("DIBAYAR"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");;


    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public static OrderStatus fromValue(String value) {
        for (OrderStatus orderStatus : values()) {
            if (orderStatus.value.equalsIgnoreCase(value)) {
                return orderStatus;
            }
        }
        return null;
    }
}
