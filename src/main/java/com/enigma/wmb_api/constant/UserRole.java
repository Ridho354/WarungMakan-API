package com.enigma.wmb_api.constant;

public enum UserRole {
    // ketika kita mendefinisikan role, harus pakai prefix "ROLE_"
    // makanya diupdate dari CUSRTOMER menjadi ROLE_CUSTOMER
    // karena sudah jadi aturan baku dari spring security (https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html)
    ROLE_CUSTOMER("PELANGGAN"),
    ROLE_ADMIN("KARYAWAN"),
    ROLE_SUPER_ADMIN("PEMILIK");


    private String value;

    UserRole(String value) {
        this.value = value;
    }

    public static UserRole fromValue(String value) {
        for (UserRole userRole : values()) {
            if (userRole.value.equalsIgnoreCase(value)) {
                return userRole;
            }
        }
        return null;
    }
}
