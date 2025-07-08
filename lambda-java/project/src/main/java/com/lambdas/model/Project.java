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

public class Project {

    @JsonProperty("id")
    @NotBlank(message = "Project ID cannot be blank")
    @Size(max = 255, message = "Project ID cannot exceed 255 characters")
    private String id;

    @JsonProperty("name")
    @NotBlank(message = "Project name cannot be blank")
    @Size(min = 1, max = 100, message = "Project name must be between 1 and 100 characters")
    private String name;

    @JsonProperty("description")
    @NotBlank(message = "Project description cannot be blank")
    @Size(min = 1, max = 500, message = "Project description must be between 1 and 500 characters")
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
    @Size(min = 1, max = 100, message = "State must be between 1 and 100 characters")
    private String state;

    @JsonProperty("country")
    @NotBlank(message = "Country cannot be blank")
    @Pattern(regexp = "^[A-Z]{2,3}$", message = "Country must be a 2 or 3 letter uppercase code")
    @Size(min = 2, max = 3, message = "Country code must be 2 or 3 characters")
    private String country;

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
    private ProjectStatus status;

    @JsonProperty("responsibleUser")
    @NotBlank(message = "Responsible user cannot be blank")
    @Size(max = 255, message = "Responsible user cannot exceed 255 characters")
    private String responsibleUser;

    @JsonProperty("dataSource")
    @NotBlank(message = "Data source cannot be blank")
    @Size(max = 255, message = "Data source cannot exceed 255 characters")
    private String dataSource;

    @JsonProperty("company")
    @NotBlank(message = "Company cannot be blank")
    @Size(max = 255, message = "Company cannot exceed 255 characters")
    private String company;

    @JsonProperty("createdBy")
    @NotBlank(message = "Created by cannot be blank")
    @Size(max = 255, message = "Created by cannot exceed 255 characters")
    private String createdBy;

    @JsonProperty("budget")
    @NotNull(message = "Budget cannot be null")
    @DecimalMin(value = "0.00", message = "Budget must be greater than or equal to 0")
    @Digits(integer = 13, fraction = 2, message = "Budget must have at most 13 integer digits and 2 decimal places")
    private BigDecimal budget;

    @JsonProperty("inventory")
    @NotBlank(message = "Inventory cannot be blank")
    @Size(min = 1, max = 500, message = "Inventory must be between 1 and 500 characters")
    private String inventory;

    // Default constructor
    public Project() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = ProjectStatus.ACTIVE;
        this.budget = BigDecimal.ZERO;
    }

    // Constructor for existing projects (when loading from database)
    public Project(String id, String name, String description, String address, String city,
                   String state, String country, LocalDateTime createdAt, LocalDateTime updatedAt,
                   ProjectStatus status, String responsibleUser, String dataSource, String company,
                   String createdBy, BigDecimal budget, String inventory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.responsibleUser = responsibleUser;
        this.dataSource = dataSource;
        this.company = company;
        this.createdBy = createdBy;
        this.budget = budget;
        this.inventory = inventory;
    }

    // Builder pattern
    public static class Builder {
        private String id, name, description, address, city, state, country;
        private String responsibleUser, dataSource, company, createdBy, inventory;
        private LocalDateTime createdAt, updatedAt;
        private ProjectStatus status;
        private BigDecimal budget;
        private boolean isNewEntity = true;

        public Builder id(String id) {
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

        public Builder responsibleUser(String responsibleUser) {
            this.responsibleUser = responsibleUser;
            return this;
        }

        public Builder dataSource(String dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder budget(BigDecimal budget) {
            this.budget = budget;
            return this;
        }

        public Builder inventory(String inventory) {
            this.inventory = inventory;
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

        public Builder status(ProjectStatus status) {
            this.status = status;
            return this;
        }

        public Builder fromDatabase() {
            this.isNewEntity = false;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.id = this.id;
            project.name = this.name;
            project.description = this.description;
            project.address = this.address;
            project.city = this.city;
            project.state = this.state;
            project.country = this.country;
            project.responsibleUser = this.responsibleUser;
            project.dataSource = this.dataSource;
            project.company = this.company;
            project.createdBy = this.createdBy;
            project.inventory = this.inventory;
            project.status = this.status != null ? this.status : ProjectStatus.ACTIVE;
            project.budget = this.budget != null ? this.budget : BigDecimal.ZERO;

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
        return countryCode.matches("^[A-Z]{2,3}$");
    }

    public boolean isValidBudget(BigDecimal budget) {
        if (budget == null) return false;
        return budget.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean hasRequiredFields() {
        return id != null && !id.trim().isEmpty() &&
               name != null && !name.trim().isEmpty() &&
               description != null && !description.trim().isEmpty() &&
               city != null && !city.trim().isEmpty() &&
               state != null && !state.trim().isEmpty() &&
               country != null && !country.trim().isEmpty() &&
               responsibleUser != null && !responsibleUser.trim().isEmpty() &&
               dataSource != null && !dataSource.trim().isEmpty() &&
               company != null && !company.trim().isEmpty() &&
               createdBy != null && !createdBy.trim().isEmpty() &&
               inventory != null && !inventory.trim().isEmpty();
    }

    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        if (!Objects.equals(this.status, status)) {
            this.status = status;
            updateTimestamp();
        }
    }

    public String getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(String responsibleUser) {
        if (!Objects.equals(this.responsibleUser, responsibleUser)) {
            this.responsibleUser = responsibleUser;
            updateTimestamp();
        }
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        if (!Objects.equals(this.dataSource, dataSource)) {
            this.dataSource = dataSource;
            updateTimestamp();
        }
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        if (!Objects.equals(this.company, company)) {
            this.company = company;
            updateTimestamp();
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        if (!Objects.equals(this.createdBy, createdBy)) {
            this.createdBy = createdBy;
            updateTimestamp();
        }
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        if (!Objects.equals(this.budget, budget)) {
            this.budget = budget;
            updateTimestamp();
        }
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        if (!Objects.equals(this.inventory, inventory)) {
            this.inventory = inventory;
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
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", status=" + status +
                ", responsibleUser='" + responsibleUser + '\'' +
                ", company='" + company + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", budget=" + budget +
                ", inventory='" + inventory + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}