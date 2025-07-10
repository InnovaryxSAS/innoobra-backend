package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChapterStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    ChapterStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ChapterStatus fromValue(String value) {
        for (ChapterStatus status : ChapterStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid chapter status value: " + value);
    }
}