package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lambdas.validation.annotations.StatusValid;
import jakarta.validation.constraints.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateRoleRequestDTO {

    @JsonProperty("name")
    @Size(min = 1, max = 50, message = "Role name must be between 1 and 50 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 100, message = "Role description cannot exceed 100 characters")
    private String description;

    @JsonProperty("status")
    @StatusValid
    private String status;

    // Default constructor
    public UpdateRoleRequestDTO() {
    }

    // Constructor para debugging
    public UpdateRoleRequestDTO( String name, String description, String status) {

        this.name = name;
        this.description = description;
        this.status = status;
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}