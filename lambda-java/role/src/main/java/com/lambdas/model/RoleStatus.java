package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    PENDING("pending"),
    SUSPENDED("suspended");

    private final String value;

    RoleStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static RoleStatus fromValue(String value) {
        for (RoleStatus status : RoleStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}