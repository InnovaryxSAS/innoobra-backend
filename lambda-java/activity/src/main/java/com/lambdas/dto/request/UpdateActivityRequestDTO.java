package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.ActivityStatusValid;
import jakarta.validation.constraints.*;

public class UpdateActivityRequestDTO {

    @JsonProperty("code")
    @Size(min = 1, max = 255, message = "Code must be between 1 and 255 characters")
    private String code;

    @JsonProperty("name")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @JsonProperty("unit")
    @Size(min = 1, max = 50, message = "Unit must be between 1 and 50 characters")
    private String unit;

    @JsonProperty("quantity")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Double quantity;

    @JsonProperty("status")
    @ActivityStatusValid
    private String status;

    // Default constructor
    public UpdateActivityRequestDTO() {
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpdateActivityRequestDTO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                '}';
    }
}