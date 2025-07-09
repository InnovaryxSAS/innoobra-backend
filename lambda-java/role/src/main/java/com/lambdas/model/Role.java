package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Role {

    @JsonProperty("id_role")
    @NotBlank(message = "Role ID cannot be blank")
    @Size(max = 255, message = "Role ID cannot exceed 255 characters")
    private String idRole;

    @JsonProperty("name")
    @NotBlank(message = "Role name cannot be blank")
    @Size(min = 1, max = 50, message = "Role name must be between 1 and 50 characters")
    private String name;

    @JsonProperty("description")
    @NotBlank(message = "Role description cannot be blank")
    @Size(min = 1, max = 100, message = "Role description must be between 1 and 100 characters")
    private String description;

    @JsonProperty("createdAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = "Created at cannot be null")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = "Updated at cannot be null")
    private LocalDateTime updatedAt;

    @JsonProperty("status")
    @NotNull(message = "Status cannot be null")
    private RoleStatus status;

    // Default constructor
    public Role() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = RoleStatus.ACTIVE;
    }

    // Constructor for existing roles (when loading from database)
    public Role(String idRole, String name, String description, LocalDateTime createdAt, 
                LocalDateTime updatedAt, RoleStatus status) {
        this.idRole = idRole;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Builder pattern
    public static class Builder {
        private String idRole, name, description;
        private LocalDateTime createdAt, updatedAt;
        private RoleStatus status;
        private boolean isNewEntity = true;

        public Builder idRole(String idRole) {
            this.idRole = idRole;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            this.isNewEntity = false;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder status(RoleStatus status) {
            this.status = status;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public Role build() {
            Role role = new Role();
            role.idRole = this.idRole;
            role.name = this.name;
            role.description = this.description;
            role.status = this.status != null ? this.status : RoleStatus.ACTIVE;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                role.createdAt = now;
                role.updatedAt = now;
            } else {
                role.createdAt = this.createdAt != null ? this.createdAt : now;
                role.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return role;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isValidStatus(String statusValue) {
        if (statusValue == null) return false;
        try {
            RoleStatus.fromValue(statusValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean hasRequiredFields() {
        return idRole != null && !idRole.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               description != null && !description.trim().isEmpty();
    }

    public boolean isValidFieldLengths() {
        return (idRole == null || idRole.length() <= 255) &&
               (name == null || (name.length() >= 1 && name.length() <= 50)) &&
               (description == null || (description.length() >= 1 && description.length() <= 100));
    }

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 50;
    }

    public boolean isValidDescription(String description) {
        return description != null && !description.trim().isEmpty() && description.length() <= 100;
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getIdRole() {
        return idRole;
    }

    public void setIdRole(String idRole) {
        if (!Objects.equals(this.idRole, idRole)) {
            this.idRole = idRole;
            updateTimestamp();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!Objects.equals(this.name, name)) {
            this.name = name;
            updateTimestamp();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!Objects.equals(this.description, description)) {
            this.description = description;
            updateTimestamp();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (this.createdAt == null) {
            this.createdAt = createdAt;
        }
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RoleStatus getStatus() {
        return status;
    }

    public void setStatus(RoleStatus status) {
        if (!Objects.equals(this.status, status)) {
            this.status = status;
            updateTimestamp();
        }
    }

    public void touch() {
        updateTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return Objects.equals(idRole, role.idRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRole);
    }

    @Override
    public String toString() {
        return "Role{" +
                "idRole='" + idRole + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}