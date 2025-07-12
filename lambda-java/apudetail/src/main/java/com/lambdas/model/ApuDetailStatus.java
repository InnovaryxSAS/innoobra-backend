package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ApuDetailStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    ApuDetailStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ApuDetailStatus fromValue(String value) {
        for (ApuDetailStatus status : ApuDetailStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid APU detail status value: " + value);
    }
}