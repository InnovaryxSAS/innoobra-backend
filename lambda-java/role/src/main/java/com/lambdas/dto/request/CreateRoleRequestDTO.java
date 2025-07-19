package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lambdas.validation.annotations.StatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRoleRequestDTO {

    @JsonProperty("name")
    @NotBlank(message = "Role name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 50, message = "Role name must be between 1 and 50 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 100, message = "Role description cannot exceed 100 characters")
    private String description;

    @JsonProperty("status")
    @StatusValid
    private String status = "active";

    // Default constructor
    public CreateRoleRequestDTO() {
    }

    // Constructor para debugging
    public CreateRoleRequestDTO( String name, String description, String status) {

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
        return "CreateRoleRequestDTO{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}