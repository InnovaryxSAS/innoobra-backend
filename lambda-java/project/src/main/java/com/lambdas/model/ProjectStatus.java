package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    PENDING("pending"),
    SUSPENDED("suspended"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    private final String value;

    ProjectStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ProjectStatus fromValue(String value) {
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}