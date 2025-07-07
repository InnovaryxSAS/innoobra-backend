package com.lambdas.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.lambdas.model.RoleStatus;

import java.time.LocalDateTime;

public class RoleResponseDTO {

    @JsonProperty("id_role")
    private String idRole;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("createdAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @JsonProperty("status")
    private RoleStatus status;

    // Default constructor
    public RoleResponseDTO() {
    }

    // Builder pattern
    public static class Builder {
        private String idRole, name, description;
        private LocalDateTime createdAt, updatedAt;
        private RoleStatus status;

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

        public RoleResponseDTO build() {
            RoleResponseDTO dto = new RoleResponseDTO();
            dto.idRole = this.idRole;
            dto.name = this.name;
            dto.description = this.description;
            dto.createdAt = this.createdAt;
            dto.updatedAt = this.updatedAt;
            dto.status = this.status;
            return dto;
        }
    }

    public static Builder builder() {
        return new Builder();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
        this.status = status;
    }

    @Override
    public String toString() {
        return "RoleResponseDTO{" +
                "idRole='" + idRole + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}