package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    PENDING("pending"),
    SUSPENDED("suspended");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static UserStatus fromValue(String value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}