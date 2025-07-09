package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.validation.annotations.RoleStatusValid;
import com.lambdas.validation.groups.ValidationGroups;
import jakarta.validation.constraints.*;

public class CreateRoleRequestDTO {

    @JsonProperty("id_role")
    @NotBlank(message = "Role ID cannot be blank", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Role ID cannot exceed 255 characters")
    @Pattern(regexp = "^[A-Za-z0-9]{1,255}$", message = "Role ID must contain only alphanumeric characters")
    private String idRole;

    @JsonProperty("name")
    @NotBlank(message = "Role name cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 50, message = "Role name must be between 1 and 50 characters")
    private String name;

    @JsonProperty("description")
    @NotBlank(message = "Role description cannot be blank", groups = ValidationGroups.Create.class)
    @Size(min = 1, max = 100, message = "Role description must be between 1 and 100 characters")
    private String description;

    @JsonProperty("status")
    @RoleStatusValid
    private String status = "active";

    // Default constructor
    public CreateRoleRequestDTO() {
    }

    // Getters and Setters
    public String getIdRole() {
        return idRole;
    }

    public void setIdRole(String idRole) {
        this.idRole = idRole;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CreateRoleRequestDTO{" +
                "idRole='" + idRole + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}