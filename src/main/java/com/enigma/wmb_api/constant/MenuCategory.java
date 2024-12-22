package com.enigma.wmb_api.constant;

public enum MenuCategory {
    FOOD("MAKANAN"),
    BEVERAGE("MINUMAN");

    private String value;

    MenuCategory(String value) {
        this.value = value;
    }

    public static MenuCategory fromValue(String value) {
        for (MenuCategory menuCategory : values()) {
            if (menuCategory.value.equalsIgnoreCase(value)) {
                return menuCategory;
            }
        }
        return null;
    }
}
