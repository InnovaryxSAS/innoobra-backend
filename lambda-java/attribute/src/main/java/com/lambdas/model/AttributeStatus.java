package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AttributeStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    AttributeStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static AttributeStatus fromValue(String value) {
        for (AttributeStatus status : AttributeStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid attribute status value: " + value);
    }
}