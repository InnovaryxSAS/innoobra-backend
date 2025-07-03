package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CompanyStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    PENDING("pending"),
    SUSPENDED("suspended");

    private final String value;

    CompanyStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static CompanyStatus fromValue(String value) {
        for (CompanyStatus status : CompanyStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}