package com.lambdas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Project {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    @NotBlank(message = "Project name cannot be blank")
    @Size(min = 1, max = 100, message = "Project name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 500, message = "Project description cannot exceed 500 characters")
    private String description;

    @JsonProperty("address")
    @Size(max = 150, message = "Address cannot exceed 150 characters")
    private String address;

    @JsonProperty("city")
    @NotBlank(message = "City cannot be blank")
    @Size(min = 1, max = 50, message = "City must be between 1 and 50 characters")
    private String city;

    @JsonProperty("state")
    @NotBlank(message = "State cannot be blank")
    @Size(min = 1, max = 50, message = "State must be between 1 and 50 characters")
    private String state;

    @JsonProperty("country")
    @NotBlank(message = "Country cannot be blank")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country must be a 2 letter uppercase code")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    private String country;

    @JsonProperty("status")
    @NotNull(message = "Status cannot be null")
    private ProjectStatus status;

    @JsonProperty("responsibleUser")
    private UUID responsibleUser;

    @JsonProperty("dataSourceId")
    private UUID dataSourceId;

    @JsonProperty("companyId")
    @NotNull(message = "Company ID cannot be null")
    private UUID companyId;

    @JsonProperty("createdBy")
    private UUID createdBy;

    @JsonProperty("budgetAmount")
    @DecimalMin(value = "0.00", message = "Budget must be greater than or equal to 0")
    @Digits(integer = 12, fraction = 2, message = "Budget must have at most 12 integer digits and 2 decimal places")
    private BigDecimal budgetAmount;

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

    // Default constructor
    public Project() {
        this.id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = ProjectStatus.ACTIVE;
        this.budgetAmount = BigDecimal.ZERO;
    }

    // Constructor for existing projects (when loading from database)
    public Project(UUID id, String name, String description, String address, String city,
                   String state, String country, ProjectStatus status, UUID responsibleUser,
                   UUID dataSourceId, UUID companyId, UUID createdBy, BigDecimal budgetAmount,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.status = status;
        this.responsibleUser = responsibleUser;
        this.dataSourceId = dataSourceId;
        this.companyId = companyId;
        this.createdBy = createdBy;
        this.budgetAmount = budgetAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern
    public static class Builder {
        private UUID id;
        private String name, description, address, city, state, country;
        private UUID responsibleUser, dataSourceId, companyId, createdBy;
        private LocalDateTime createdAt, updatedAt;
        private ProjectStatus status;
        private BigDecimal budgetAmount;
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

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder status(ProjectStatus status) {
            this.status = status;
            return this;
        }

        public Builder responsibleUser(UUID responsibleUser) {
            this.responsibleUser = responsibleUser;
            return this;
        }

        public Builder dataSourceId(UUID dataSourceId) {
            this.dataSourceId = dataSourceId;
            return this;
        }

        public Builder companyId(UUID companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder createdBy(UUID createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder budgetAmount(BigDecimal budgetAmount) {
            this.budgetAmount = budgetAmount;
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

        public Project build() {
            Project project = new Project();
            project.id = this.id != null ? this.id : UUID.randomUUID();
            project.name = this.name;
            project.description = this.description;
            project.address = this.address;
            project.city = this.city;
            project.state = this.state;
            project.country = this.country;
            project.status = this.status != null ? this.status : ProjectStatus.ACTIVE;
            project.responsibleUser = this.responsibleUser;
            project.dataSourceId = this.dataSourceId;
            project.companyId = this.companyId;
            project.createdBy = this.createdBy;
            project.budgetAmount = this.budgetAmount != null ? this.budgetAmount : BigDecimal.ZERO;

            LocalDateTime now = LocalDateTime.now();
            
            if (isNewEntity) {
                project.createdAt = now;
                project.updatedAt = now;
            } else {
                project.createdAt = this.createdAt != null ? this.createdAt : now;
                project.updatedAt = this.updatedAt != null ? this.updatedAt : now;
            }
            
            return project;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isValidStatus(String statusValue) {
        if (statusValue == null) return false;
        try {
            ProjectStatus.fromValue(statusValue);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidCountryCode(String countryCode) {
        if (countryCode == null) return false;
        return countryCode.matches("^[A-Z]{2}$");
    }

    public boolean isValidBudget(BigDecimal budget) {
        if (budget == null) return false;
        return budget.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean hasRequiredFields() {
        return name != null && !name.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               state != null && !state.trim().isEmpty() &&
               country != null && !country.trim().isEmpty() &&
               companyId != null;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (!Objects.equals(this.address, address)) {
            this.address = address;
            updateTimestamp();
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (!Objects.equals(this.city, city)) {
            this.city = city;
            updateTimestamp();
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (!Objects.equals(this.state, state)) {
            this.state = state;
            updateTimestamp();
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (!Objects.equals(this.country, country)) {
            this.country = country;
            updateTimestamp();
        }
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        if (!Objects.equals(this.status, status)) {
            this.status = status;
            updateTimestamp();
        }
    }

    public UUID getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(UUID responsibleUser) {
        if (!Objects.equals(this.responsibleUser, responsibleUser)) {
            this.responsibleUser = responsibleUser;
            updateTimestamp();
        }
    }

    public UUID getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(UUID dataSourceId) {
        if (!Objects.equals(this.dataSourceId, dataSourceId)) {
            this.dataSourceId = dataSourceId;
            updateTimestamp();
        }
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        if (!Objects.equals(this.companyId, companyId)) {
            this.companyId = companyId;
            updateTimestamp();
        }
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        if (!Objects.equals(this.createdBy, createdBy)) {
            this.createdBy = createdBy;
            updateTimestamp();
        }
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        if (!Objects.equals(this.budgetAmount, budgetAmount)) {
            this.budgetAmount = budgetAmount;
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
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", status=" + status +
                ", responsibleUser=" + responsibleUser +
                ", dataSourceId=" + dataSourceId +
                ", companyId=" + companyId +
                ", createdBy=" + createdBy +
                ", budgetAmount=" + budgetAmount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}