package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.RoleStatusValid;
import jakarta.validation.constraints.*;

public class UpdateRoleRequestDTO {

    @JsonProperty("name")
    @Size(min = 1, max = 50, message = "Role name must be between 1 and 50 characters")
    private String name;

    @JsonProperty("description")
    @Size(min = 1, max = 100, message = "Role description must be between 1 and 100 characters")
    private String description;

    @JsonProperty("status")
    @RoleStatusValid
    private String status;

    // Default constructor
    public UpdateRoleRequestDTO() {
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpdateRoleRequestDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}