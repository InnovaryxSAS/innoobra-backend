package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

public class Attribute {

    @JsonProperty("idAttribute")
    @NotBlank(message = "Attribute ID cannot be blank")
    @Size(max = 255, message = "Attribute ID cannot exceed 255 characters")
    private String idAttribute;

    @JsonProperty("idCompany")
    @NotBlank(message = "Company ID cannot be blank")
    @Size(max = 255, message = "Company ID cannot exceed 255 characters")
    private String idCompany;

    @JsonProperty("code")
    @NotBlank(message = "Code cannot be blank")
    @Size(min = 1, max = 100, message = "Code must be between 1 and 100 characters")
    private String code;

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @JsonProperty("unit")
    @NotBlank(message = "Unit cannot be blank")
    @Size(min = 1, max = 50, message = "Unit must be between 1 and 50 characters")
    private String unit;

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
    private AttributeStatus status;

    // Default constructor
    public Attribute() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = AttributeStatus.ACTIVE;
    }

    // Constructor for existing attributes (when loading from database)
    public Attribute(String idAttribute, String idCompany, String code, String name, 
                    String description, String unit, LocalDateTime createdAt, 
                    LocalDateTime updatedAt, AttributeStatus status) {
        this.idAttribute = idAttribute;
        this.idCompany = idCompany;
        this.code = code;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    // Builder pattern
    public static class Builder {
        private String idAttribute, idCompany, code, name, description, unit;
        private LocalDateTime createdAt, updatedAt;
        private AttributeStatus status;
        private boolean isNewEntity = true;

        public Builder idAttribute(String idAttribute) {
            this.idAttribute = idAttribute;
            return this;
        }

        public Builder idCompany(String idCompany) {
            this.idCompany = idCompany;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
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

        public Builder unit(String unit) {
            this.unit = unit;
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

        public Builder status(AttributeStatus status) {
            this.status = status;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public Attribute build() {
            Attribute attribute = new Attribute();
            attribute.idAttribute = this.idAttribute;
            attribute.idCompany = this.idCompany;
            attribute.code = this.code;
            attribute.name = this.name;
            attribute.description = this.description;
            attribute.unit = this.unit;
            attribute.status = this.status != null ? this.status : AttributeStatus.ACTIVE;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                attribute.createdAt = now;
                attribute.updatedAt = now;
            } else {
                attribute.createdAt = this.createdAt != null ? this.createdAt : now;
                attribute.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return attribute;
        }
    }

    // Validation methods
    public boolean isValidStatus(String statusValue) {
        if (statusValue == null) return false;
        try {
            AttributeStatus.fromValue(statusValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidCode(String code) {
        return code != null && !code.trim().isEmpty() && code.trim().length() <= 100;
    }

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.trim().length() <= 100;
    }

    public boolean isValidDescription(String description) {
        return description == null || description.trim().length() <= 200;
    }

    public boolean isValidUnit(String unit) {
        return unit != null && !unit.trim().isEmpty() && unit.trim().length() <= 50;
    }

    public boolean hasRequiredFields() {
        return idAttribute != null && !idAttribute.trim().isEmpty() &&
               idCompany != null && !idCompany.trim().isEmpty() &&
               code != null && !code.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               unit != null && !unit.trim().isEmpty();
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(String idAttribute) {
        if (!Objects.equals(this.idAttribute, idAttribute)) {
            this.idAttribute = idAttribute;
            updateTimestamp();
        }
    }

    public String getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(String idCompany) {
        if (!Objects.equals(this.idCompany, idCompany)) {
            this.idCompany = idCompany;
            updateTimestamp();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (!Objects.equals(this.code, code)) {
            this.code = code;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        if (!Objects.equals(this.unit, unit)) {
            this.unit = unit;
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

    public AttributeStatus getStatus() {
        return status;
    }

    public void setStatus(AttributeStatus status) {
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
        Attribute attribute = (Attribute) o;
        return Objects.equals(idAttribute, attribute.idAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAttribute);
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "idAttribute='" + idAttribute + '\'' +
                ", idCompany='" + idCompany + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", unit='" + unit + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}