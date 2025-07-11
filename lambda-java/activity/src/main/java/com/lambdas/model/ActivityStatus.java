package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    ActivityStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ActivityStatus fromValue(String value) {
        for (ActivityStatus status : ActivityStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid activity status value: " + value);
    }
}