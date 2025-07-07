package com.lambdas.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambdas.model.RoleStatus;

public class CreateRoleRequestDTO {

    @JsonProperty("id_role")
    private String idRole;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private RoleStatus status;

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

    public RoleStatus getStatus() {
        return status;
    }

    public void setStatus(RoleStatus status) {
        this.status = status;
    }

    // Builder pattern
    public static class Builder {
        private final CreateRoleRequestDTO dto;

        public Builder() {
            dto = new CreateRoleRequestDTO();
        }

        public Builder idRole(String idRole) {
            dto.setIdRole(idRole);
            return this;
        }

        public Builder name(String name) {
            dto.setName(name);
            return this;
        }

        public Builder description(String description) {
            dto.setDescription(description);
            return this;
        }

        public Builder status(RoleStatus status) {
            dto.setStatus(status);
            return this;
        }

        public CreateRoleRequestDTO build() {
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "CreateRoleRequestDTO{" +
                "idRole='" + idRole + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}