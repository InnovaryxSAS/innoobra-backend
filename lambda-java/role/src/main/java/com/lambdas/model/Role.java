package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Role {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    @NotBlank(message = "Role name cannot be blank")
    @Size(min = 1, max = 50, message = "Role name must be between 1 and 50 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 100, message = "Role description cannot exceed 100 characters")
    private String description; 

    @JsonProperty("status")
    @NotNull(message = "Status cannot be null")
    private RoleStatus status;

    @JsonProperty("created_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = "Created at cannot be null")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull(message = "Updated at cannot be null")
    private LocalDateTime updatedAt;

    // Default constructor
    public Role() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Constructor for existing roles (when loading from database)
    public Role(UUID id, String name, String description, RoleStatus status,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern
    public static class Builder {
        private UUID id;
        private String name, description;
        private RoleStatus status;
        private LocalDateTime createdAt, updatedAt;
        private boolean isNewEntity = true;

        public Builder id(UUID id) {
            this.id = id;
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

        public Builder status(RoleStatus status) {
            this.status = status;
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

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public Role build() {
            Role role = new Role();
            role.id = this.id != null ? this.id : UUID.randomUUID();
            role.name = this.name;
            role.description = this.description;
            role.status = this.status;

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
        return name != null && !name.trim().isEmpty() &&
               status != null;
    }

    public boolean isValidFieldLengths() {
        return (name == null || (name.length() >= 1 && name.length() <= 50)) &&
               (description == null || description.length() <= 100);
    }

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 50;
    }

    public boolean isValidDescription(String description) {
        return description == null || description.length() <= 100;
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        if (!Objects.equals(this.id, id)) {
            this.id = id;
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

    public RoleStatus getStatus() {
        return status;
    }

    public void setStatus(RoleStatus status) {
        if (!Objects.equals(this.status, status)) {
            this.status = status;
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
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}